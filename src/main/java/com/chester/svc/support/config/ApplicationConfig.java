package com.chester.svc.support.config;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.db.model.AuthRule;
import com.chester.svc.auth.db.repository.AuthRuleDao;
import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.db.repository.MenuRepository;
import com.chester.svc.sys.db.repository.RoleRepository;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.coll.Lists;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.*;

@Component
public class ApplicationConfig implements ApplicationRunner {
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private MenuRepository menuRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuthRuleDao authRuleDao;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private ApplicationContext applicationContext;

    public List<AuthRule> getAllURL() {
        List<AuthRule> authRuleList = new ArrayList<>();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
            AuthRule authRule = new AuthRule();
            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();
            PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            authRule.setPath(p.getPatterns().iterator().next());
            for(RequestMethod method :methodsCondition.getMethods()){
                authRule.setType(method.toString());
            }
            if(authRule.getType()==null){
                continue;
            }
            parseRolesAnnotation(authRule,handlerMethod.getMethod().getDeclaredAnnotations());
            authRuleList.add(authRule);
        }
        return authRuleList;
    }

    private void parseRolesAnnotation(AuthRule authRule, Annotation[] annotations){
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Roles) {
                    Roles roles = (Roles) annotation;
                    authRule.setRoles(roles.value());
                    authRule.setModify(roles.modify());
                    authRule.setRemark(roles.remark());
                }
            }
        }
    }

    /**
     * 初始化
     */
    @Override
    public void run(ApplicationArguments args) {
        List<Role> allRoles = roleRepository.findAll();
        if(Lists.isEmpty(allRoles)){
            Role role_admin = new Role(1L,"admin","管理员",false);
            Role role_authed = new Role(2L,"authed","登录用户",false);
            Role role_operator = new Role(3L,"operator","操作员",false);
            authRuleDao.saveAll(getAllURL());
            roleRepository.saveAll(Arrays.asList(role_admin,role_authed,role_operator));
            List<Role> roles_admin = Collections.singletonList(role_admin);
            List<Role> roles_admin_operator = Arrays.asList(role_admin,role_operator);
            Menu menu_1 = new Menu(1L,"sys-root","系统管理","mdi-home-account","/sys","/sys",10,false,null,roles_admin);
            Menu menu_2 = new Menu(2L,"sys-root","用户管理","mdi-home-account","/sys/users","/sys/users",1,false,"sys-root",roles_admin);
            Menu menu_3 = new Menu(3L,"sys-root","菜单管理","mdi-menu","/sys/menus","/sys/menus",2,false,"sys-root",roles_admin);
            Menu menu_4 = new Menu(4L,"sys-root","角色管理","mdi-file-account-outline","/sys/roles","/sys/roles",3,false,"sys-root",roles_admin);
            Menu menu_5 = new Menu(5L,"sys-root","路由管理","mdi-widgets-outline","/sys/rules","/sys/rules",4,false,"sys-root",roles_admin);
            Menu menu_6 = new Menu(6L,"plc-root-data","主数据管理","mdi-alpha-r-circle-outline","/plc/data","/plc/data",1,false,null,roles_admin_operator);
            Menu menu_7 = new Menu(7L,"plc-machines","设备管理","mdi-apache-kafka","/data/machines","/plc/machines",1,false,"plc-root-data",roles_admin_operator);
            Menu menu_8 = new Menu(8L,"plc-materials","物料维护","mdi-database-refresh","/data/materials","/plc/materials",3,false,"plc-root-data",roles_admin_operator);
            Menu menu_9 = new Menu(9L,"plc-root-task","生产管理","mdi-alpha-r-circle-outline","/plc/task","/plc/task",1,false,null,roles_admin_operator);
            Menu menu_10 = new Menu(10L,"plc-missions","任务管理","mdi-screw-machine-flat-top","/task/missions","/plc/missions",2,false,"plc-root-task",roles_admin_operator);
            Menu menu_11 = new Menu(11L,"plc-jobs","排程管理","mdi-database-refresh","/task/jobs","/plc/jobs",4,false,"plc-root-task",roles_admin_operator);
            menuRepository.saveAll(Arrays.asList(menu_1,menu_2,menu_3,menu_4,menu_5,menu_6,menu_7,menu_8,menu_9,menu_10,menu_11));
            String pwd = passwordEncoder.encode("123456");
            User user = new User(1L,"100000","管理员","男",new Date(),null,"18888888888",pwd,roles_admin,false);
            userRepository.save(user);
        }
    }
}