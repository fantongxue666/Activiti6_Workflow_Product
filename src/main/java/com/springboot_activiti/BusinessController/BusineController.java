package com.springboot_activiti.BusinessController;

import com.github.pagehelper.PageInfo;
import com.springboot_activiti.BusinessMapper.BusineMapper;
import com.springboot_activiti.BusinessMapper.PowerMapper;
import com.springboot_activiti.BusinessService.BusineService;
import com.springboot_activiti.BusinessService.PowerService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName ActivitiController.java
 * @Description TODO
 * @createTime 2020年05月16日 10:46:00
 */
@Controller
@RequestMapping("/business")
public class BusineController {

    @Autowired
    BusineService busineService;
    @Autowired
    PowerService powerService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    //后台主页
    @RequestMapping("/index")
    public String index(){
        return "back/index";
    }

    //流程定义管理列表
//    @RequestMapping("/list")
//    public String list(){
//        return "back/processDefinition";
//    }
    //编辑示例
    @RequestMapping("/toEdit")
    public String toEdit(){
        return "back/house_edit";
    }
    //楼盘状态示例
    @RequestMapping("/loupan")
    public String loupan(){
        return "back/loupanchart";
    }
    //欢迎页
    @RequestMapping("/welcome")
    public String welcome(){
        return "back/introduce";
    }
    //仪表盘
    @RequestMapping("/yibiaopan")
    public String ybp(){
        return "back/yibiaopan";
    }
    //资源共享平台
    @RequestMapping("/zygx")
    public String zygx(){
        return "back/share";
    }
    @RequestMapping("/zygl")
    public String zygl(Model model,Integer pn){
        PageInfo<Map> shareList = powerService.getShareList(pn);
        model.addAttribute("list",shareList);
        return "back/zygl";
    }
    @ResponseBody
    @RequestMapping("/insertZy")
    public Integer insertZy(String name, String url,String pwd,String kind){
        Map map=new HashMap();
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        map.put("id",id);
        map.put("name",name);
        map.put("url",url);
        map.put("pwd",pwd);
        map.put("kind",kind);
        map.put("time",new Date());
        int i = busineMapper.insertShare(map);
        return i;
    }

    @ResponseBody
    @RequestMapping("/delZy")
    public Integer delZy(String id){
        return busineService.delShare(id);
    }

    @Autowired
    PowerMapper powerMapper;
    @ResponseBody
    @RequestMapping("/getZY")
    public Map getZY(){
        List<Map> shareList = powerMapper.getShareList();
        Map map=new HashMap();
        List<Map> spzy = shareList.stream().filter(s -> "视频资源".equals(s.get("kind"))).collect(Collectors.toList());
        List<Map> syrj = shareList.stream().filter(s -> "实用软件".equals(s.get("kind"))).collect(Collectors.toList());
        List<Map> dy = shareList.stream().filter(s -> "电影".equals(s.get("kind"))).collect(Collectors.toList());
        List<Map> qt = shareList.stream().filter(s -> "其他".equals(s.get("kind"))).collect(Collectors.toList());
        map.put("spzy",spzy);
        map.put("syrj",syrj);
        map.put("dy",dy);
        map.put("qt",qt);
        return map;
    }


    /**
     * todo 取登录名称
     */
    @RequestMapping("/getSession")
    @ResponseBody
    public String getSession(HttpSession session){
        String name=(String) session.getAttribute("account");
        return name;
    }

    @Autowired
    BusineMapper busineMapper;

    /**
     * todo 登录请求
     */
    @PostMapping("/loginSubmit")
    public String loginSubmit(String username, String password, HttpSession session, Model model){
        List<Map> accountByPassword = busineService.getAccountByPassword(username, password);
        if(accountByPassword.size()>0){
            //用户存在
            session.setAttribute("account",username);
            model.addAttribute("account",username);

            String sessionid = session.getId();
            int i = busineMapper.updateSessionIdByUsername(sessionid, username);


            return "redirect:/business/index";
        }else{
            //用户不存在
            model.addAttribute("msg","用户名或密码错误");
            return "login";
        }
    }

    /**
     * todo 退出系统
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        String username=(String) session.getAttribute("account");
        int i = busineMapper.updateSessionIdByUsername(null, username);

        session.removeAttribute("account");
        return "forward:/business/login";
    }

    /**
     * todo 退出系统
     */
    @RequestMapping("/logout1")
    public String logout1(HttpSession session){
        session.removeAttribute("account");
        return "forward:/business/login";
    }


    //轮询检查sesiosnid是否被替换，如果被替换，说明该账号已在异地登录
    @RequestMapping("/checkSessionId")
    @ResponseBody
    public int checkSessionId(HttpSession session){
        Map map = busineMapper.checkSessionId(session.getId());
        if(map!=null){
            //没有被替换，账号状态正常
            return 1;
        }else{
            //账号已在异地登录，掉线通知
            return 0;
        }
    }

}
