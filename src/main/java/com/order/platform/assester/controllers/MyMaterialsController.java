package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.MaterialFormDTO;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.services.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/my/materials")
@RequiredArgsConstructor
public class MyMaterialsController {
    private final MaterialService materialService;

    @Audited
    @GetMapping
    public String list(Authentication authentication, Model model) {
        model.addAttribute("materials", materialService.findMine(authentication.getName()));
        return "my-materials";
    }

    @Audited
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("materialForm", new MaterialFormDTO());
        return "material-form";
    }

    @Audited
    @PostMapping
    public String publish(@Valid @ModelAttribute("materialForm") MaterialFormDTO form, BindingResult bindingResult,
                          @RequestParam("files") List<MultipartFile> files, Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) return "material-form";
        materialService.publish(form.getTitle(), form.getDescription(), files, authentication.getName());
        return "redirect:/my/materials";
    }
}
