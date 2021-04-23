package com.softwaretest.demo.Service;

import com.softwaretest.demo.Controller.Request.FinePaymentRequest;
import com.softwaretest.demo.Controller.Response.AccountDetailsResponse;
import com.softwaretest.demo.Controller.Response.AccountResponse;
import com.softwaretest.demo.DemoApplication;
import com.softwaretest.demo.Domain.Account;
import com.softwaretest.demo.Domain.Flow;
import com.softwaretest.demo.Domain.Installment;
import com.softwaretest.demo.Domain.Loan;
import com.softwaretest.demo.Repository.AccountRepository;
import com.softwaretest.demo.Repository.FlowRepository;
import com.softwaretest.demo.Repository.InstallmentRepository;
import com.softwaretest.demo.Repository.LoanRepository;
import org.apache.shiro.util.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest(classes = DemoApplication.class)
public class LoanServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private LoanService loanService;


    /*
    @description: 用来插入测试数据，成功后需要注释掉，以防正式测试时测试失败
     */

    @Test
    void insertAccountA(){
        //存第一个账号，作为主测试账号，有贷款，而且余额总量远大于贷款总量,用于测试自动还款和手动还款成功的情况
        Account account1 = new Account();
        account1.setIdNumber("123456");
        account1.setType("储蓄");
        account1.setBalance(200000.00);
        account1.setCustomerName("郭泰安");
        accountRepository.save(account1);


        //第一笔贷款，用于测试手动还款
        Loan loan = new Loan();
        loan.setAccountId(account1.getAccountId());
        loan.setAmount(12000.00);
        loan.setInterestRate(0.05);
        loan.setStartDate(castStringToTimeStamp("2021-02-28 00:00:00"));

        Installment installment1 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-03-28 00:00:00"));
        Installment installment2 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-04-28 00:00:00"));
        Installment installment3 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-05-28 00:00:00"));
        List<Installment> installments = new LinkedList<>();
        installmentRepository.save(installment1);
        installmentRepository.save(installment2);
        installmentRepository.save(installment3);
        installments.add(installment1); installments.add(installment2); installments.add(installment3);
        loan.setInstallments(installments);
        loanRepository.save(loan);
        Flow flow1 = new Flow("贷款发放",account1.getAccountId(),12000.00,castStringToTimeStamp("2021-02-28 00:00:00"));
        flowRepository.save(flow1);

        //第二笔贷款，用于测试自动还款
        Loan loan2 = new Loan();
        loan2.setAccountId(account1.getAccountId());
        loan2.setAmount(12000.00);
        loan2.setInterestRate(0.05);
        loan2.setStartDate(castStringToTimeStamp("2021-02-28 00:00:00"));

        Installment installment4 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-03-29 00:00:00"));
        Installment installment5 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-04-29 00:00:00"));
        Installment installment6 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-05-29 00:00:00"));
        List<Installment> installments2 = new LinkedList<>();
        installmentRepository.save(installment4);
        installmentRepository.save(installment5);
        installmentRepository.save(installment6);
        installments2.add(installment4); installments2.add(installment5); installments2.add(installment6);
        loan2.setInstallments(installments2);
        loanRepository.save(loan2);

        Flow flow2 = new Flow("贷款发放",account1.getAccountId(),12000.00,castStringToTimeStamp("2021-02-28 00:00:00"));
        flowRepository.save(flow2);
    }

    /*
    @description: 用来插入测试数据，成功后需要注释掉，以防正式测试时测试失败
     */

    @Test
    void insertAccountB(){
        //存第二个账号，其余额较少，用于测试自动还款和手动还款失败的情况
        Account account2 = new Account();
        account2.setIdNumber("567890");
        account2.setType("储蓄");
        account2.setBalance(100.00);
        accountRepository.save(account2);

        Loan loan3 = new Loan();
        loan3.setAccountId(account2.getAccountId());
        loan3.setAmount(12000.00);
        loan3.setInterestRate(0.05);
        loan3.setStartDate(castStringToTimeStamp("2021-02-28 00:00:00"));
        Installment installment7 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-03-28 00:00:00"));
        Installment installment8 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-04-28 00:00:00"));
        Installment installment9 = new Installment(4200.00,4200.00,castStringToTimeStamp("2021-05-28 00:00:00"));
        List<Installment> installments3 = new LinkedList<>();
        installmentRepository.save(installment7);
        installmentRepository.save(installment8);
        installmentRepository.save(installment9);
        installments3.add(installment7); installments3.add(installment8); installments3.add(installment9);
        loan3.setInstallments(installments3);
        loanRepository.save(loan3);

        Flow flow2 = new Flow("贷款发放",account2.getAccountId(),12000.00,castStringToTimeStamp("2021-02-28 00:00:00"));
        flowRepository.save(flow2);
    }

    /*
    @Description : 幂等操作
     */
    @Test
    void testIdentityCheck(){
        List<AccountResponse> responses1 = loanService.getAccounts("12345678");
        System.out.println(responses1.size());
        Assert.isTrue(responses1.size() == 0);

        List<AccountResponse> responses2 = loanService.getAccounts("123456");
        Assert.isTrue(responses2.size() ==accountRepository.findByIdNumber("123456").size());
        Assert.isTrue(responses2.get(0).getCustomerName().equals("郭泰安") );
    }

    @Test
    void testGetLoans(){
        List<Account> accounts = accountRepository.findByIdNumber("123456");
        Assert.isTrue(accounts.size() == 1);
        Long accountId = accounts.get(0).getAccountId();

        //先用一个肯定不存在的账号
        List<AccountDetailsResponse> response1 = loanService.getLoans(accountId+10001);
        Assert.isTrue(response1==null);


        //再用一个已经注册的账号
        List<AccountDetailsResponse> responses2 = loanService.getLoans(accountId);
        Assert.isTrue(responses2.size() == 2);
        Assert.isTrue(responses2.get(0).getInstallments().size() == 3);
        Assert.isTrue(responses2.get(0).getFine() == 210.00);

    }

    @Test
    void testPayFine(){
        //测试成功归还罚款
        List<Account> accounts = accountRepository.findByIdNumber("123456");
        Long accountId = accounts.get(0).getAccountId();
        List<AccountDetailsResponse> responses2 = loanService.getLoans(accountId);
        Loan loan1 =loanRepository.findById( responses2.get(0).getLoanId()).orElse(null);
        Assert.isTrue(loan1!=null);
        boolean isPayFineSuccess1 = loanService.payFine(loan1.getId(),responses2.get(0).getFine());
        Assert.isTrue(isPayFineSuccess1);

        //测试贷款Id不存在
        boolean isPayFineSuccess2 = loanService.payFine(234567898L,0.00);
        Assert.isTrue(!isPayFineSuccess2);

        //测试余额不足，缴纳罚款失败
        List<Account> accounts2 = accountRepository.findByIdNumber("567890");
        Long accountId2 = accounts2.get(0).getAccountId();
        List<AccountDetailsResponse> responses3 = loanService.getLoans(accountId2);
        Loan loan2 =loanRepository.findById( responses3.get(0).getLoanId()).orElse(null);
        Assert.isTrue(loan2!=null);
        boolean isPayFineSuccess3 = loanService.payFine(loan2.getId(),responses3.get(0).getFine());
        Assert.isTrue(!isPayFineSuccess3);
    }

    @Test
    void testRepayment(){
        List<Account> accounts1 = accountRepository.findByIdNumber("123456");
        Long accountId1 = accounts1.get(0).getAccountId();
        List<AccountDetailsResponse> accountDetails1 = loanService.getLoans(accountId1);
        //成功手动还款
        String repaymentResult1 = loanService.repay(accountId1,accountDetails1.get(0).getLoanId(),0,4200.00);
        Assert.isTrue(repaymentResult1.equals("success"));

        //账户不存在
        String repaymentResult2 = loanService.repay(accountId1+1230601,accountDetails1.get(0).getLoanId(),0,4200.00);
        Assert.isTrue(repaymentResult2.equals("付款账号不存在"));

        //贷款id不存在
        String repaymentResult3 = loanService.repay(accountId1,accountDetails1.get(0).getLoanId()+1230601,0,4200.00);
        Assert.isTrue(repaymentResult3.equals("贷款不存在"));

        //分期索引非法
        String repaymentResult4 = loanService.repay(accountId1,accountDetails1.get(0).getLoanId(),-1,4200.00);
        Assert.isTrue(repaymentResult4.equals("分期索引非法"));

        //测试余额不足的情况
        List<Account> accounts2 = accountRepository.findByIdNumber("567890");
        Long accountId2 = accounts2.get(0).getAccountId();
        List<AccountDetailsResponse> accountDetails2= loanService.getLoans(accountId2);
        String repaymentResult5 = loanService.repay(accountId2,accountDetails2.get(0).getLoanId(),0,4200.00);
        Assert.isTrue(repaymentResult5.equals("余额不足"));
    }

    private Timestamp castStringToTimeStamp(String timeStr){
        return Timestamp.valueOf(timeStr);
    }

}
