package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.MaterialFormDTO;
import com.order.platform.assester.logging.EmailMasker;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.services.MaterialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/my/materials")
@RequiredArgsConstructor
@Validated
public class MyMaterialsController {
    private final MaterialService materialService;

    @Audited
    @GetMapping
    public String list(Authentication authentication, Model model) {
        model.addAttribute("materials", materialService.findMyMaterialAsSummaries(authentication.getName()));
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
    public String publish(@Valid @ModelAttribute("materialForm") MaterialFormDTO form,
                          BindingResult bindingResult,
                          @NotNull @RequestParam("files") List<MultipartFile> files,
                          Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "material-form";
        }

        materialService.publish(form, files, authentication.getName());

        log.info("Material published by user {}; fileCount={}",
                EmailMasker.mask(authentication.getName()), files.size());

        return "redirect:/my/materials";
    }
}
