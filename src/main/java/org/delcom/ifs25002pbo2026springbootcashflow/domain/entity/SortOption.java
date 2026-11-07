package org.delcom.ifs25002pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi.
 * Setiap opsi membawa comparator-nya sendiri sehingga logika pengurutan
 * terpusat di domain, bukan tersebar sebagai angka ajaib di view/use case.
 */
public enum SortOption {
    /** Jumlah terkecil lebih dahulu. */
    AMOUNT_ASC("Jumlah Terkecil", Comparator.comparing(Transaction::getAmount)),

    /** Jumlah terbesar lebih dahulu. */
    AMOUNT_DESC("Jumlah Terbesar", Comparator.comparing(Transaction::getAmount).reversed()),

    /** Tampilkan pemasukan terlebih dahulu. */
    INCOME_FIRST("Pemasukan Dulu", Comparator.comparing(Transaction::getType)),

    /** Tampilkan pengeluaran terlebih dahulu. */
    EXPENSE_FIRST("Pengeluaran Dulu", Comparator.comparing(Transaction::getType).reversed());

    /** Label yang mudah dibaca untuk ditampilkan pada pilihan urutan. */
    private final String label;

    /** Comparator yang digunakan untuk mengurutkan daftar transaksi. */
    private final Comparator<Transaction> comparator;

    SortOption(String label, Comparator<Transaction> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    /** Mengembalikan label tampilan opsi ini. */
    public String label() {
        return label;
    }

    /** Mengembalikan comparator yang sesuai dengan opsi ini. */
    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
