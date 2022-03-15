package ru.nvn.springdatajpahibernate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nvn.springdatajpahibernate.dao.BankAccountDAO;
import ru.nvn.springdatajpahibernate.exception.BankTransactionException;
import ru.nvn.springdatajpahibernate.form.SendMoneyForm;
import ru.nvn.springdatajpahibernate.models.BankAccountInfo;

import java.util.List;

@Controller
public class MainController {
		@Autowired
		private BankAccountDAO bankAccountDAO;

		@RequestMapping(value = "/", method = RequestMethod.GET)
		public String showBankAccounts(Model model) {
				model.addAttribute("accountInfos", bankAccountDAO.listBankAccountInfo());
				return "accountsPage";
		}

		@RequestMapping(value = "/sendMoney", method = RequestMethod.GET)
		public String viewSendMoneyPage(Model model) {
				SendMoneyForm form = new SendMoneyForm(1L, 2L, 700d);
				model.addAttribute("sendMoneyForm", form);
				return "sendMoneyPage";
		}

		@RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
		public String processSendMoney(Model model, SendMoneyForm sendMoneyForm) {
				System.out.println("Send Money::" + sendMoneyForm.getAmount());
				try {
						bankAccountDAO.sendMoney(sendMoneyForm.getFromAccountId(), //
								sendMoneyForm.getToAccountId(), //
								sendMoneyForm.getAmount());
				} catch (BankTransactionException e) {
						model.addAttribute("errorMessage", "Error: " + e.getMessage());
						return "sendMoneyPage";
				}
				return "redirect:/";
		}
}