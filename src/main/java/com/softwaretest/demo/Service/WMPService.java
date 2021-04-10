package com.softwaretest.demo.Service;

import com.softwaretest.demo.Controller.Request.BuyWMPRequest;
import com.softwaretest.demo.Domain.Account;
import com.softwaretest.demo.Domain.Loan;
import com.softwaretest.demo.Domain.WMP;
import com.softwaretest.demo.Repository.AccountRepository;
import com.softwaretest.demo.Repository.LoanRepository;
import com.softwaretest.demo.Repository.WMPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WMPService {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WMPRepository wmpRepository;

    public int buyWMP(BuyWMPRequest request) {
        Account account = accountRepository.findAccountByAccountId(request.getAccountId());
        List<Loan> loans = loanRepository.findByAccountId(request.getAccountId());
        double sum = 0;
        for (Loan loan : loans) {
            sum += loanService.getFine(loan);
        }
        //判断是否有罚金
        if (sum > 0) {
            //还完已有罚金
            if (account.getBalance() < sum) return 1;
            for (Loan loan : loans) {
                loanService.payFine(loan.getId(), loanService.getFine(loan));
            }
            account.setBalance(account.getBalance() - sum);
        }
        //判断账户余额
        if (account.getBalance() < request.getAmount()) return 2;

        account.setBalance(account.getBalance()-request.getAmount());
        accountRepository.save(account);

        WMP wmp = new WMP(request.getAccountId(),request.getTitle(),request.getType(),request.getAmount(), request.getNumber(),request.getStartDate(),request.getEndDate());
        wmpRepository.save(wmp);

        return 0;
    }

}
