package com.anuragnepal.Telegrambot.Repository;

import com.anuragnepal.Telegrambot.Entity.Expense;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Integer> {


    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    Double getSumOfAmountBetweenDates( LocalDate startDate, LocalDate endDate);


    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.date = :today")
    Double getSumOfAmountForToday( LocalDate today);

    @Query("SELECT CONCAT(e.nameofexpense, ' ', e.amount) " +
            "FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    List<String> findNameAndAmountAsString( LocalDate startDate, LocalDate endDate);

}