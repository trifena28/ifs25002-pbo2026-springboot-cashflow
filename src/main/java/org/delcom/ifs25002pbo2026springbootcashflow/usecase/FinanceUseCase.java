package org.delcom.ifs25002pbo2026springbootcashflow.usecase;

import org.delcom.ifs25002pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Use case yang menangani logika bisnis aplikasi cashflow.
 * Tidak melakukan I/O (tanpa Vaadin / System.out). Hanya memproses data dan melempar
 * checked exception domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi yang tersedia. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambahkan pemasukan baru setelah validasi keterangan dan jumlah. */
    public Transaction addIncome(String description, String amount) throws ValidationException {
        String cleanDescription = validateDescription(description);
        BigDecimal parsedAmount = parseAmount(amount);
        return transactionRepository.saveIncome(cleanDescription, parsedAmount);
    }

    /** Menambahkan pengeluaran baru setelah validasi keterangan dan jumlah. */
    public Transaction addExpense(String description, String amount) throws ValidationException {
        String cleanDescription = validateDescription(description);
        BigDecimal parsedAmount = parseAmount(amount);
        return transactionRepository.saveExpense(cleanDescription, parsedAmount);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException {
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan seluruh transaksi sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(SortOption option) {
        return sortTransactions(option, transactionRepository.findAll());
    }

    /** Mengurutkan daftar transaksi tertentu (misalnya hasil pencarian) sesuai {@link SortOption}. */
    public List<Transaction> sortTransactions(SortOption option, List<Transaction> source) {
        return source.stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Total seluruh pemasukan. */
    public BigDecimal getTotalIncome() {
        return transactionRepository.findBy(Transaction::isIncome).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Total seluruh pengeluaran. */
    public BigDecimal getTotalExpense() {
        return transactionRepository.findBy(Transaction::isExpense).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Saldo saat ini: total pemasukan dikurangi total pengeluaran. */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    /** Memastikan keterangan tidak kosong dan mengembalikan versi yang sudah di-trim. */
    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return description.trim();
    }

    /** Mengubah teks jumlah menjadi BigDecimal positif (2 digit desimal). */
    private BigDecimal parseAmount(String amount) throws ValidationException {
        if (amount == null || amount.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        BigDecimal parsed;
        try {
            parsed = new BigDecimal(amount.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah harus berupa angka");
        }
        parsed = parsed.setScale(2, RoundingMode.HALF_UP);
        if (parsed.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
        return parsed;
    }
}
