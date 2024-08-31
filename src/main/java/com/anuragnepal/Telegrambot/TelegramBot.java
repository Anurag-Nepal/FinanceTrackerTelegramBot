package com.anuragnepal.Telegrambot;
import com.anuragnepal.Telegrambot.Entity.Expense;
import com.anuragnepal.Telegrambot.Repository.ExpenseRepository;
import com.fasterxml.jackson.databind.util.LinkedNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TelegramBot extends TelegramLongPollingBot {
    List<Long> AUTHORIZED_USER_IDS = List.of(1625561424L);
    private final ExpenseRepository expenseRepository;

    @Autowired
    public TelegramBot(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
        if (this.expenseRepository == null) {
            throw new RuntimeException("ExpenseRepository is not initialized in constructor");
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        long userId = update.getMessage().getFrom().getId();
        if (update.hasMessage() && update.getMessage().hasText()&&isAuthorized(userId)) {

            {
                String messageofUser = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();


                    if (messageofUser.equals("/week")) {
                        sendResponse(chatId, getweekTotal());
                        return;
                    }


                    if (messageofUser.equals("/month")) {
                        sendResponse(chatId, getmonthTotal());
                        return;
                    }

                    if (messageofUser.equals("/deleteall")) {
                        sendResponse(chatId, ClearHistory());
                        return;
                    }
                    if (messageofUser.equals("/today")) {
                        sendResponse(chatId, getToday());
                        return;
                    }

                    if (messageofUser.equals("/weeklist")) {
                        String result = String.join(System.lineSeparator(), getWeekly());
                        sendResponse(chatId, result);
                        return;
                    }
                    if (messageofUser.equals("/monthlist")) {
                        String result = String.join(System.lineSeparator(), getMonthly());
                        sendResponse(chatId, result);
                        return;
                    } else {
                        // Process the input as an expense entry
                        String[] parts = messageofUser.split(" ", 2);
                        if (parts.length == 2) {
                            String name = parts[0];
                            String amountStr = parts[1];
                            try {
                                double amount = Double.parseDouble(amountStr);
                                storeExpense(name, amount);
                                sendResponse(chatId, "Expense recorded successfully!");
                            } catch (NumberFormatException e) {
                                sendResponse(chatId, "Invalid amount. Please enter a valid number.");
                            }
                        } else {
                            sendResponse(chatId, "Invalid format. Please enter in the format: Name Amount");
                        }
                    }
                }
            }
        }

            public void storeExpense (String name,double amount){
                if (expenseRepository == null) {
                    throw new RuntimeException("ExpenseRepository is not initialized");
                }
                try {
                    Expense expense = new Expense();
                    expense.setNameofexpense(name);
                    expense.setAmount(amount);
                    expense.setDate(LocalDate.now());
                    expenseRepository.save(expense);
                } catch (Exception e) { // Catch more general exceptions to identify the root cause
                    // Log the original exception message for debugging
                    throw new RuntimeException("Error saving expense: " + e.getMessage(), e);
                }
            }


            @Override
            public String getBotUsername () {
                return "Anuragsbot"; // Replace with your bot's username
            }

            @Override
            public String getBotToken () {
                return "6293992659:AAFW5ozZG1R0oWXex4IKQsbu_1N-3bF_d70"; // Replace with your bot's token
            }

            private void sendResponse ( long chatId, String text){
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(text);
                try {
                    execute(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public String getweekTotal ()
            {
                Double total = expenseRepository.getSumOfAmountBetweenDates(LocalDate.now().minusDays(7), LocalDate.now());
                String hey = String.valueOf(total);
                return hey;
            }

            public String getmonthTotal ()
            {
                Double total = expenseRepository.getSumOfAmountBetweenDates(LocalDate.now().minusDays(30), LocalDate.now());
                String hey = String.valueOf(total);
                return hey;
            }

            public String getToday ()
            {
                Double total = expenseRepository.getSumOfAmountForToday(LocalDate.now());
                String hey = String.valueOf(total);
                return hey;
            }

            public String ClearHistory ()
            {
                expenseRepository.deleteAll();
                return "All the Expenses in Database are Deleted Successfully";
            }

            public List<String> getWeekly ()
            {
                return expenseRepository.findNameAndAmountAsString(LocalDate.now().minusDays(7), LocalDate.now());
            }
            public List<String> getMonthly ()
            {
                return expenseRepository.findNameAndAmountAsString(LocalDate.now().minusDays(30), LocalDate.now());
            }


            private boolean isAuthorized ( long userId){
                return AUTHORIZED_USER_IDS.contains(userId);
            }

        }
