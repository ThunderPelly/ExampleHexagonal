package com.example.hexagonal.adapter.out.transaction

import com.example.hexagonal.port.out.TransactionalPort
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class SpringTransactionalAdapter(private val transactionManager: PlatformTransactionManager) : TransactionalPort {

    private lateinit var transactionStatus: TransactionStatus

    override fun beginTransaction() {
        val transactionDefinition = DefaultTransactionDefinition()
        transactionStatus = transactionManager.getTransaction(transactionDefinition)
    }

    override fun commitTransaction() {
        transactionManager.commit(transactionStatus)
    }

    override fun rollbackTransaction() {
        transactionManager.rollback(transactionStatus)
    }
}