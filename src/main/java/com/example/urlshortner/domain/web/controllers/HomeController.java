package com.example.urlshortner.domain.web.controllers;


import com.example.urlshortner.ApplicationProperties;
import com.example.urlshortner.domain.entities.ShortUrl;
import com.example.urlshortner.domain.entities.User;
import com.example.urlshortner.domain.exceptions.ShortUrlNotFoundException;
import com.example.urlshortner.domain.models.CreateShortUrlCmd;
import com.example.urlshortner.domain.models.PagedResult;
import com.example.urlshortner.domain.models.ShortUrlDto;
import com.example.urlshortner.domain.services.ShortUrlService;
import com.example.urlshortner.domain.web.dtos.CreateShortUrlForm;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;
    private final SecurityUtils securityUtils;

    public HomeController(ShortUrlService shortUrlService, ApplicationProperties properties, SecurityUtils securityUtils) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;
        this.securityUtils = securityUtils;
    }

    // get /?page=0&size=10&sprt=createdAt,desc
    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "1") Integer page,
            Model model) {
        this.addShortUrlDataToModel(model,page);
        model.addAttribute("paginationUrl","/");
        model.addAttribute("createShortUrlForm",new CreateShortUrlForm("",false,null));
        return "index";
    }

    private void addShortUrlDataToModel(Model model,int pageNo){
        PagedResult<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls(pageNo, properties.pageSize());
        model.addAttribute("shortUrls",shortUrls);
        model.addAttribute("baseUrl",properties.baseUrl());
    }
    @PostMapping("/short-urls")
    String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm createShortUrlForm,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if(bindingResult.hasErrors()) {
            this.addShortUrlDataToModel(model,1);
            return "index";
        }

        try{
            Long userId = securityUtils.getCurrentUserId();
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(createShortUrlForm.originalUrl(),
                    createShortUrlForm.isPrivate(),
                    createShortUrlForm.expirationInDays(),
                    userId
                    );
            var shortUrlDto = shortUrlService.createShortUrl(cmd);
            redirectAttributes.addFlashAttribute("successMessage","Short url created successfully"+
                    " "+properties.baseUrl()+"/s/"+shortUrlDto.shortKey());
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage","Failed to create short url");
        }

        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey) {
        Long userId = securityUtils.getCurrentUserId();
        Optional<ShortUrlDto> shortUrlDtoOptional = shortUrlService.accessShortUrl(shortKey,userId);

        if(shortUrlDtoOptional.isEmpty()){
            throw new ShortUrlNotFoundException("Invalid Short Key: "+shortKey);
        }

        ShortUrlDto shortUrlDto = shortUrlDtoOptional.get();
        return "redirect:"+shortUrlDto.originalUrl();
    }

    @PostMapping("/delete-urls")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public String deleteUrls(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "No URLs selected for deletion");
            return "redirect:/my-urls";
        }
        try {
            var currentUserId = securityUtils.getCurrentUserId();
            shortUrlService.deleteUserShortUrls(ids, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Selected URLs have been deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting URLs: " + e.getMessage());
        }
        return "redirect:/my-urls";
    }

    @GetMapping("/my-urls")
    public String showUserUrls(
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        var currentUserId = securityUtils.getCurrentUserId();
        PagedResult<ShortUrlDto> myUrls =
                shortUrlService.getUserShortUrls(currentUserId, page, properties.pageSize());
        model.addAttribute("shortUrls", myUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/my-urls");
        return "my-urls";
    }
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/login")
    String loginForm(){
        return "login";
    }

}
