package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.MaterialSummaryDTO;
import com.order.platform.assester.dto.MaterialFileDTO;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.services.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialCatalogController {
    private final MaterialService materialService;

    @Audited
    @GetMapping
    public String catalog(@RequestParam(required = false) String query,
                          @RequestParam(required = false) String code, Model model) {
        model.addAttribute("query", query == null ? "" : query.trim());
        model.addAttribute("code", code == null ? "" : code.trim());
        MaterialSummaryDTO searchedMaterial = code == null || code.isBlank() ? null : materialService.findPublishedByCode(code);
        model.addAttribute("searchedMaterial", searchedMaterial);
        model.addAttribute("materials", code == null || code.isBlank() ? materialService.findPublished(query)
                : searchedMaterial == null ? List.of() : List.of(searchedMaterial));
        return "materials-catalog";
    }

    @Audited
    @GetMapping("/code/{code}")
    public String byCode(@PathVariable String code, Model model) {
        MaterialSummaryDTO material = materialService.findPublishedByCode(code);
        if (material == null) return "redirect:/materials?codeNotFound=true";
        return "redirect:/materials/" + material.id();
    }

    @Audited
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("material", materialService.findPublishedById(id));
        model.addAttribute("files", materialService.getPublishedFiles(id));
        return "material-details";
    }

    @Audited
    @GetMapping("/{materialId}/files/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long materialId, @PathVariable Long fileId) {
        Resource file = materialService.getPublishedFile(materialId, fileId);
        MaterialFileDTO details = materialService.getPublishedFileDetails(materialId, fileId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" +
                        org.springframework.web.util.UriUtils.encode(details.originalName(), java.nio.charset.StandardCharsets.UTF_8))
                .body(file);
    }
}
