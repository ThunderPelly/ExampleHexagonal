package com.example.hexagonal.adapter.out.transaction

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus

class SpringTransactionalAdapterTest {

    private val transactionManager: PlatformTransactionManager = mockk()
    private val transactionStatus: TransactionStatus = mockk()

    @InjectMocks
    private lateinit var transactionalAdapter: SpringTransactionalAdapter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        transactionalAdapter = SpringTransactionalAdapter(transactionManager)
    }

    @Test
    fun `beginTransaction should start a new transaction`() {
        // Arrange
        every { transactionManager.getTransaction(any()) } returns transactionStatus

        // Act
        transactionalAdapter.beginTransaction()

        // Assert
        verify(exactly = 1) { transactionManager.getTransaction(any()) }
    }

    @Test
    fun `commitTransaction should commit the current transaction`() {
        // Arrange
        every { transactionManager.getTransaction(any()) } returns transactionStatus
        every { transactionManager.commit(any()) } just Runs

        // Act
        transactionalAdapter.beginTransaction()
        transactionalAdapter.commitTransaction()

        // Assert
        verify(exactly = 1) { transactionManager.commit(transactionStatus) }
    }

    @Test
    fun `rollbackTransaction should rollback the current transaction`() {
        // Arrange
        every { transactionManager.getTransaction(any()) } returns transactionStatus
        every { transactionManager.rollback(any()) } just Runs

        // Act
        transactionalAdapter.beginTransaction()
        transactionalAdapter.rollbackTransaction()

        // Assert
        verify(exactly = 1) { transactionManager.rollback(transactionStatus) }
    }

    @Test
    fun `beginTransaction, commitTransaction, and rollbackTransaction should be called in the correct order`() {
        // Arrange
        every { transactionManager.getTransaction(any()) } returns transactionStatus
        every { transactionManager.commit(any()) } just Runs
        every { transactionManager.rollback(any()) } just Runs

        // Act
        transactionalAdapter.beginTransaction()
        transactionalAdapter.commitTransaction()
        transactionalAdapter.rollbackTransaction()

        // Assert
        verifyOrder {
            transactionManager.getTransaction(any())
            transactionManager.commit(transactionStatus)
            transactionManager.rollback(transactionStatus)
        }
    }
}