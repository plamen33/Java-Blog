package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.NotificationService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private NotificationService notifyService;


    @GetMapping("/")
    public String index(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal().equals("anonymousUser")){
            List<Category> categories = this.categoryRepository.findAll();
            model.addAttribute("view", "home/index");
            model.addAttribute("categories", categories);
            return "base-layout";
        }
        else{

            List<Category> categories = this.categoryRepository.findAll();
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());

            model.addAttribute("user", user);
            model.addAttribute("view", "home/index");
            model.addAttribute("categories", categories);
            return "base-layout";
        }

    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model){
        model.addAttribute("view", "error/403");

        return "base-layout";
    }

    @GetMapping("/category/{id}")
    public String listArticles(Model model, @PathVariable Integer id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal().equals("anonymousUser")){
            if(!this.categoryRepository.exists(id)){
                return "redirect:/";
            }

            Category category=this.categoryRepository.findOne(id);
            Set<Article> articles = category.getArticles();
            model.addAttribute("articles", articles);
            model.addAttribute("category", category);
            model.addAttribute("view", "home/list-articles");
            return "base-layout";
        }
        else{
            if(!this.categoryRepository.exists(id)){
                return "redirect:/";
            }
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = this.userRepository.findByEmail(principal.getUsername());

            Category category=this.categoryRepository.findOne(id);
            Set<Article> articles = category.getArticles();
            model.addAttribute("articles", articles);
            model.addAttribute("category", category);
            model.addAttribute("user", user);
            model.addAttribute("view", "home/list-articles");
            return "base-layout";
        }
    }


}