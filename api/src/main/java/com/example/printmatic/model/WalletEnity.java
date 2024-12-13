package com.example.printmatic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class WalletEnity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Default constructor required by JPA
    public WalletEnity() {
    }

    public WalletEnity(UserEntity user) {
        this.user = user;
        this.balance = 0.0; // Initial balance
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public synchronized void addFunds(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }
        this.balance += amount;
    }

    public synchronized boolean withdrawFunds(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to withdraw must be positive.");
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", balance=" + balance +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletEnity wallet = (WalletEnity) o;
        return id != null && id.equals(wallet.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
