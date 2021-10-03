import java.util.HashMap;
import java.util.Scanner;

public class Bank {
	public static void main(String args[]) {
		Scanner sc=new Scanner(System.in);
		HashMap <Double, Person> customers= new HashMap<>();
		while(true) {
			System.out.println("\n******************************");
			System.out.println("********Main Menu************");
			System.out.println("Enter corresponding number");
			System.out.println("1. Add new customer");
			System.out.println("2. Delete a customer");
			System.out.println("3. Show all customers");
			System.out.println("4. Show all customers and their accounts");
			System.out.println("5. Customer Menu");
			System.out.println("6. Exit");
			
			String entry=sc.nextLine();
			try {
				int choice=Integer.valueOf(entry);
				if(choice==6) {
					break;
				}
				mainMenu(choice, customers);
			}
			catch(NumberFormatException e) {
				System.out.println("Enter a number between 1 and 6");
			}
		}
	}
	static void mainMenu(int choice, HashMap<Double,Person> customers) {
		Scanner sc=new Scanner(System.in);
		if(choice==1) {
			System.out.println("Enter customer salary");
			Person p;
			try {
				double salary=sc.nextDouble();
				p=new Person(salary);
			}
			catch(Exception e) {
				System.out.println("Please enter the salary");
				return;
			}
			customers.put(p.getID(),p);
		}
		else if(choice==2) {
			double id=sc.nextDouble();
			customers.get(id);
		}
	}
}

//Person Class
class Person{
	protected double id;
	private double salary;
	private long mortgagePayAccount;
	private double repaymentTime;

	private boolean hasMortgage;
	
	protected HashMap<Long,Current>currentAccounts;
	protected HashMap<Long,Saver>saverAccounts;
	protected HashMap<Long, Mortgage> mortgageAccount;
	
	private static double lastUsed=1000;
	
	Person(double salary){
		this.salary=salary;
		id=lastUsed++;
		hasMortgage=false;
	}
	
	double getID() {
		return id;
	}
	
	double getSalary() {
		return salary;
	}
	
	double getLastUsed() {
		return lastUsed;
	}
	
	void addCurrentAccount() {
		Current current=new Current();
		currentAccounts.put(current.getAccountNumber(),current);
	}
	
	void addSaverAccount() {
		Saver saver=new Saver();
		saverAccounts.put(saver.getAccountNumber(),saver);
	}
	
	boolean checkMortgage() {
		return hasMortgage;
	}
	
	long getMortgagePayAccount() {
		return mortgagePayAccount;
	}
	
	double getrepaymentTime() {
		return repaymentTime;
	}
	
	void addMortgageAccount(double repaymentTime,long mortgagePayAccount) {
		hasMortgage=true;
		this.repaymentTime=repaymentTime;
		this.mortgagePayAccount=mortgagePayAccount;
		Mortgage mortgage=new Mortgage();
		mortgageAccount.put(mortgage.getAccountNumber(), mortgage);
	}
	
	boolean removeMortgageAccount(long accountNumber) {
		if(mortgageAccount.get(accountNumber).balance==0) {
			mortgageAccount.remove(accountNumber);
			hasMortgage=false;
			return true;
		}
		return false;
	}
	
	boolean removeCurrentAccount(long accountNumber) {
		if(currentAccounts.get(accountNumber).balance==0) {
			currentAccounts.remove(accountNumber);
			return true;
		}
		return false;
	}
	
	boolean removeSaverAccount(long accountNumber) {
		if(saverAccounts.get(accountNumber).balance==0) {
			saverAccounts.remove(accountNumber);
			return true;
		}
		return false;
	}
	
}

//Account class
class Account{
	
	protected double balance;
	protected long accountNumber;
	protected static long lastUsed=1000000000;
	
	Account(){
		balance=0;
	}
	
	double deposit(double amount) {
		balance+=amount;
		return balance;
	}
	
	double getbalance() {
		return balance;
	}
	
	long getAccountNumber() {
		return accountNumber;
	}
}

//Current Class
class Current extends Account{
	
	Current(){
		super();
		accountNumber=lastUsed++;
	}
	
	double withdraw(double amount) {
		balance-=amount;
		return balance;
	}
	
	double sendMoney(double amount, Person p, long acountNumber, boolean current) {
		balance-=amount;
		if(current) {
			Current receiver= p.currentAccounts.get(accountNumber);
			receiver.deposit(amount);
		}
		else {
			Saver receiver= p.saverAccounts.get(accountNumber);
			receiver.deposit(amount);
		}
		return balance;
	}
}

//Saver Class
class Saver extends Account{
	Saver(){
		super();
		accountNumber=lastUsed++;
	}
}

//Mortgage Class
class Mortgage extends Account{
	
	Mortgage(){
		super();
	}
	
	double takeMortgage(double amount, Person p, long acountNumber, boolean current,double time) {
		balance-=((amount*100.5*time)/100);
		if(current) {
			Current receiver= p.currentAccounts.get(accountNumber);
			receiver.deposit(amount);
		}
		else {
			Saver receiver= p.saverAccounts.get(accountNumber);
			receiver.deposit(amount);
		}
		return balance;
	}
	
	//cronjob for time period user wants
	double payMortgage(double amount, Person p, long acountNumber) {
		balance+=amount;
		Current receiver= p.currentAccounts.get(accountNumber);
		receiver.withdraw(amount);
		return balance;
	}
}