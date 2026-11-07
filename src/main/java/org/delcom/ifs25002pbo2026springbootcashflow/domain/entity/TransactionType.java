package org.delcom.ifs25002pbo2026springbootcashflow.domain.entity;

/**
 * Jenis transaksi keuangan: pemasukan atau pengeluaran.
 */
public enum TransactionType {
    /** Uang masuk (menambah saldo). */
    INCOME("Pemasukan"),

    /** Uang keluar (mengurangi saldo). */
    EXPENSE("Pengeluaran");

    /** Label yang mudah dibaca untuk ditampilkan. */
    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    /** Mengembalikan label tampilan (Pemasukan / Pengeluaran). */
    public String label() {
        return label;
    }
}
