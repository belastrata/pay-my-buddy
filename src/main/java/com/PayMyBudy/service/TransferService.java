package com.PayMyBudy.service;

import com.PayMyBudy.model.Transfer;
import com.PayMyBudy.model.User;
import com.PayMyBudy.repository.AccountRepository;
import com.PayMyBudy.repository.TransferRepository;
import com.PayMyBudy.repository.UserRepository;
import com.PayMyBudy.service.form.AccueilForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("TransferService")
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, UserRepository userRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transfer> findTransactions() {
        org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User connectedUser = userRepository.findUserByMail(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("user with email  not found"));
        return transferRepository.findTransferByUserId(connectedUser.getId());
    }


    public void transfer(AccueilForm form) {
        if (form != null) {
            org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User connectedUser = userRepository.findUserByMail(springUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("user with email  not found"));
            User to = userRepository.findUserByMail(form.getTo())
                    .orElseThrow(() -> new RuntimeException("user with email  not found"));
            Transfer transfer = new Transfer();
            transfer.setDate(LocalDateTime.now());
            transfer.setAmountBeforeFee(form.getAmount());
            transfer.setAmountAfterFee(form.getAmount() - form.getAmount() * 0.005);
            transfer.setFrom(connectedUser);
            transfer.setTo(to);


            accountRepository.save(connectedUser.getAccount().minus(transfer.getAmountBeforeFee()));
            accountRepository.save(to.getAccount().plus(transfer.getAmountAfterFee()));
            transferRepository.save(transfer);


        } else {

        }

    }
}