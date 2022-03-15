package ru.nvn.springdatajpahibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.nvn.springdatajpahibernate.entity.BankAccount;
import ru.nvn.springdatajpahibernate.exception.BankTransactionException;
import ru.nvn.springdatajpahibernate.models.BankAccountInfo;

import java.util.List;

@Repository
@Transactional
public class BankAccountDAO
{

		@Autowired
		private SessionFactory sessionFactory;


		public BankAccountDAO() {
		}
		public BankAccount findById(Long id) {
				Session session = this.sessionFactory.getCurrentSession();
				return session.get(BankAccount.class, id);
		}
		public List<BankAccountInfo> listBankAccountInfo() {
				System.out.println("----------listBankAccounts");
				String sql = "Select new " + BankAccountInfo.class.getName() //
						+ "(e.id,e.fullName,e.balance) " //
						+ " from " + BankAccount.class.getName() + " e ";
				Session session = this.sessionFactory.getCurrentSession();
				Query<BankAccountInfo> query = session.createQuery(sql, BankAccountInfo.class);
				return query.getResultList();
		}

		// MANDATORY: Transaction must be created before.
		@Transactional(propagation = Propagation.MANDATORY)
		public void addAmount(Long id, double amount) throws BankTransactionException
		{
				BankAccount account = this.findById(id);
				if (account == null) {
						throw new BankTransactionException("Account not found " + id);
				}
				double newBalance = account.getBalance() + amount;
				if (account.getBalance() + amount < 0) {
						throw new BankTransactionException(
								"The money in the account '" + id + "' is not enough (" + account.getBalance() + ")");
				}
				account.setBalance(newBalance);
		}

		// Do not catch BankTransactionException in this method.
		@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = BankTransactionException.class)
		public void sendMoney(Long fromAccountId, Long toAccountId, double amount) throws BankTransactionException {
				addAmount(toAccountId, amount);
				addAmount(fromAccountId, -amount);
		}
}
