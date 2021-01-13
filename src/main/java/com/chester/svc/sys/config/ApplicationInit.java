package com.chester.svc.sys.config;

import com.chester.svc.sys.annotation.Roles;
import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.model.Rule;
import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.db.repository.AuthRuleDao;
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
import java.util.stream.Collectors;

@Component
public class ApplicationInit implements ApplicationRunner {
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
    @Resource
    private CustomSecurityMetadataSource customSecurityMetadataSource;

    private List<Rule> getAllURL() {
        List<Rule> authRuleList = new ArrayList<>();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
            Rule authRule = new Rule();
            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();
            PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            authRule.setPath(p.getPatterns().iterator().next());
            for (RequestMethod method : methodsCondition.getMethods()) {
                authRule.setType(method.toString());
            }
            if (authRule.getType() == null) {
                continue;
            }
            parseRolesAnnotation(authRule, handlerMethod.getMethod().getDeclaredAnnotations());
            authRuleList.add(authRule);
        }
        return authRuleList;
    }

    private void parseRolesAnnotation(Rule authRule, Annotation[] annotations) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Roles) {
                    Roles roles = (Roles) annotation;
                    authRule.setInitRole(roles.value());
                    authRule.setModify(roles.modify());
                    authRule.setRemark(roles.remark());
                }
            }
        }
    }

    private List<Menu> getAllMenu() {
        Menu menu_1 = new Menu(1L, "sys-root", "系统管理", "mdi-home-account", "/sys", "/sys", 7, false, null);
        Menu menu_2 = new Menu(2L, "sys-user", "用户管理", "mdi-home-account", "/sys/users", "/sys/users", 8, false, "sys-root");
        Menu menu_3 = new Menu(3L, "sys-role", "角色管理", "mdi-file-account-outline", "/sys/roles", "/sys/roles", 9, false, "sys-root");
        Menu menu_4 = new Menu(4L, "plc-root-data", "主数据管理", "mdi-alpha-r-circle-outline", "/plc/data", "/plc/data", 1, false, null);
        Menu menu_5 = new Menu(5L, "plc-machines", "设备管理", "mdi-apache-kafka", "/data/machines", "/plc/machines", 2, false, "plc-root-data");
        Menu menu_6 = new Menu(6L, "plc-materials", "物料维护", "mdi-database-refresh", "/data/materials", "/plc/materials", 3, false, "plc-root-data");
        Menu menu_7 = new Menu(7L, "plc-root-task", "生产管理", "mdi-alpha-r-circle-outline", "/plc/task", "/plc/task", 4, false, null);
        Menu menu_8 = new Menu(8L, "plc-missions", "任务管理", "mdi-screw-machine-flat-top", "/task/missions", "/plc/missions", 5, false, "plc-root-task");
        Menu menu_9 = new Menu(9L, "plc-jobs", "排程管理", "mdi-database-refresh", "/task/jobs", "/plc/jobs", 6, false, "plc-root-task");
        return Arrays.asList(menu_1, menu_2, menu_3, menu_4, menu_5, menu_6, menu_7, menu_8, menu_9);
    }

    private List<Role> getAllRole() {
        List<Rule> rules = authRuleDao.findAll();
        customSecurityMetadataSource.setSource(rules);
        List<Menu> menus = menuRepository.findAll();
        Role role_admin = new Role(1L, "admin", "管理员", false,
                new HashSet<>(menus),
                rules.stream().filter(v -> v.getInitRole().contains("admin")).collect(Collectors.toSet()));
        Role role_operator = new Role(2L, "operator", "操作员", false,
                menus.stream().filter(v -> v.getPath().contains("plc")).collect(Collectors.toSet()),
                rules.stream().filter(v -> v.getInitRole().contains("operator")).collect(Collectors.toSet()));
        return Arrays.asList(role_admin, role_operator);
    }

    private User getUser() {
        String pwd = passwordEncoder.encode("123456");
        return new User(100000L, "管理员", "男", new Date(), null, "18888888888", pwd,
                roleRepository.findAll().stream().filter(v -> v.getName().equals("admin")).collect(Collectors.toSet())
                , false);
    }

    /**
     * 初始化
     */
    @Override
    public void run(ApplicationArguments args) {
        List<User> users = userRepository.findAll();
        if (Lists.isEmpty(users)) {
            authRuleDao.saveAll(getAllURL());
            menuRepository.saveAll(getAllMenu());
            roleRepository.saveAll(getAllRole());
            userRepository.save(getUser());
        }
    }
}