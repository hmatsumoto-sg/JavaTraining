package com.s_giken.training.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.s_giken.training.webapp.exception.NotFoundException;
import com.s_giken.training.webapp.model.entity.Charge;
import com.s_giken.training.webapp.model.entity.ChargeSearchCondition;
import com.s_giken.training.webapp.service.IChargeService;

@Controller
@RequestMapping("/charge")
public class ChargeController {
    private final IChargeService chargeService;

public ChargeController(IChargeService chargeService) {
    this.chargeService = chargeService;
}

//料金検索画面を表示する
@GetMapping("/search")
public String showSearchCondition(Model model) {
    var chargeSearchCondition = new ChargeSearchCondition();
    model.addAttribute( "chargeSearchCondition", chargeSearchCondition);
    return "charge-search-condition";
}

//料金検索結果を表示する
@PostMapping("/search")
public String searchAndLisitong(
    @ModelAttribute("chargeSearchCondition") ChargeSearchCondition chargeSearchCondition,
    Model model) {
    var result = chargeService.findByConditions(chargeSearchCondition);
model.addAttribute("result", result);
return "charge_search_result";   
}

//料金情報編集画面を表示する
@GetMapping("/edit/{id}")
public String editCharge(
    @PathVariable Long id,
    Model model) {
    var charge = chargeService.findById(id);
    if (!charge.isPresent()) {
        throw new NotFoundException(String.format("指定したchargeId(%d)の加入者情報が存在しません。", id));
    }
    model.addAttribute("isAddMode", false);
    model.addAttribute("charge", charge.get());
    return "charge_edit";
    }

//料金情報登録画面を表示する
@GetMapping("/add")
public String formAddCharge(Model model) {
    var charge = new Charge();
    model.addAttribute("isAddMode", true);
    model.addAttribute("charge", charge);
    return "charge_edit";
}

//料金情報を登録する
@PostMapping("/add")
    @Transactional
    public String addCharge(
            @Validated Charge charge,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
                // 日付が両方入力されている場合のみ比較を行う
     if (charge.getStartDate() != null && charge.getEndDate() != null) {
         if (charge.getEndDate().isBefore(charge.getStartDate())) {
            bindingResult.rejectValue("endDate", "error.date.range", "適用終了日は適用開始日以降の日付を入力してください。");
       }
   }
        if (bindingResult.hasErrors()) {
            model.addAttribute("isAddMode", true);
            return "charge_edit";
        }
        chargeService.add(charge);
        redirectAttributes.addFlashAttribute("message", "保存しました。");
        return "redirect:/charge/edit/" + charge.getChargeId();
    }

    //編集した料金情報を更新する
    @PostMapping("/update")
    @Transactional
    public String saveCharge(
            @Validated Charge charge,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

// 日付が両方入力されている場合のみ比較を行う
if (charge.getStartDate() != null && charge.getEndDate() != null) {
       if (charge.getEndDate().isBefore(charge.getStartDate())) {
             bindingResult.rejectValue("endDate", "error.date.range", "適用終了日は適用開始日以降の日付を入力してください。");
        }
 }

        if (bindingResult.hasErrors()) {
            boolean isAddMode = (charge.getChargeId() == null);
            model.addAttribute("isAddMode", isAddMode);
            return "charge_edit";
        }
        chargeService.update(charge);
        redirectAttributes.addFlashAttribute("message", "更新しました。");
        return "redirect:/charge/edit/" + charge.getChargeId();
    }

    //料金情報を削除する
    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteCharge(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        var charge = chargeService.findById(id);
        if (!charge.isPresent()) {
            throw new NotFoundException(String.format("指定したchargeId(%d)の加入者情報が存在しません。", id));
        }

        chargeService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "削除しました。");
        return "redirect:/charge/search";
    }
}
