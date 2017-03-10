package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.CommentBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Comment;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.UserRepository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Controller
public class CommentController {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;


    @GetMapping("/comment/create/{id}")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model, @PathVariable Integer id){    // public String create(@PathVariable Integer id, Model model){
        Article article = this.articleRepository.findOne(id);
        User author = article.getAuthor();
        //model.addAttribute("id", id);
        model.addAttribute("view", "/comment/create");
        model.addAttribute("article", article);

        return "base-layout";
    }
    @PostMapping("/comment/create/{id}")
    public String createProcess(CommentBindingModel commentBindingModel, @PathVariable Integer id){
        Article article = this.articleRepository.findOne(id);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());
        String date = (ZonedDateTime.now()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)).toString();
        Comment comment=new Comment(
                date,
                commentBindingModel.getCommentString(),
                article,
                user
        );
        this.commentRepository.saveAndFlush(comment);

        return "redirect:/article/"+id;
    }
}