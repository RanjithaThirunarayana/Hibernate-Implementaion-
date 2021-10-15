package hibernate1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateExample {

	static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernate-examples");
	static EntityManager entityManager = entityManagerFactory.createEntityManager();

	public static void main(String[] args) throws IOException {
		mainMenu();
		entityManager.close();
		entityManagerFactory.close();
	}
	
	private static void mainMenu() {
		
		System.out.println("Valley Art School Management System\r\n" + "\r\n" + "s) List Students\r\n"
				+ "g) List Groups\r\n" + "x) Exit\r\n" + "Please enter your choice:");

		Scanner scanner = new Scanner(System.in);
		String option = (scanner.nextLine());
		if (option.equalsIgnoreCase("s")) {
			studentMenu();
		} else if (option.equalsIgnoreCase("g")) {
			groupMenu();
		} else if (option.equalsIgnoreCase("x"))
			System.exit(0);
		else
			System.out.println("Invalid input\n");
	}

	private static void displayStudents() {
		System.out.println("Valley Art School Management System - Students\r\n" + "");
		List<Student> students = entityManager.createQuery("from Student").getResultList();
		for (Student student : students)
			System.out.println(student.getName() + "," + " " + student.getBirthYear());
	}

	private static void displayGroups() {
		System.out.println("Valley Art School Management System - Groups\r\n" + "");
		List<StudentGroup> groups = entityManager.createQuery("from StudentGroup").getResultList();
		for (StudentGroup group : groups)
			System.out.println("      " + group.getId() + ")" + " " + group.getName());
	}
	
	private static void displayGroupMembers(int value) {
		EntityManagerFactory entityManagerFactoryNew = Persistence.createEntityManagerFactory("hibernate-examples");
		EntityManager entityManagerNew = entityManagerFactoryNew.createEntityManager();
		StudentGroup groupAdded = entityManagerNew.find(StudentGroup.class, value);
		System.out.println("Valley Art School Management System - " + groupAdded.getName() + "\r\n");
		if(groupAdded.getStudents() != null) {
			for (Student student : groupAdded.getStudents()) {
				System.out.println("      " + student.getId() + ")" + " " + student.getName() + "," + " "
						+ student.getBirthYear());
			} 
		}
		entityManagerFactoryNew.close();
		entityManagerNew.close();
		groupMenu();
	}

	private static void studentMenu() {
		displayStudents();
		System.out.println(
				"\nb) Back to Main Menu\r\n" + "n) New Student\r\n" + "x) Exit\r\n" + "Please enter your choice:");
		Scanner scanner = new Scanner(System.in);
		String choice1 = scanner.nextLine();
		if (choice1.equalsIgnoreCase("b")) {
			mainMenu();
		} else if (choice1.equalsIgnoreCase("n")) {
			System.out.println("Please enter student name");
			String name = scanner.nextLine();
			System.out.println("and enter the birth year of the student");
			String year = scanner.nextLine();
			Student student = new Student();
			student.setName(name);
			student.setBirthYear(Integer.valueOf(year));
			entityManager.getTransaction().begin();
			student = entityManager.merge(student);
			entityManager.getTransaction().commit();
			displayStudents();
			System.out.println("\n");
			mainMenu();

		} else if (choice1.equalsIgnoreCase("x")) {
			System.exit(0);
		} else {
			System.out.println("Invalid choice entry");
			System.exit(0);
		}
	}

	private static void groupMenu() {
		Scanner scanner = new Scanner(System.in);
		displayGroups();
		System.out.println(
				"\nb) Back to Main Menu\r\n" + "n) New Group\r\n" + "x) Exit\r\n" + "Please enter your choice:");
		String choice = scanner.nextLine();
		if (choice.equalsIgnoreCase("b")) {
			mainMenu();
		} else if (choice.equalsIgnoreCase("n")) {
			System.out.println("Please enter Group name");
			String gName = scanner.nextLine();
			StudentGroup group = new StudentGroup();
			group.setName(gName);
			entityManager.getTransaction().begin();
			group = entityManager.merge(group);
			entityManager.getTransaction().commit();
			System.out.println("\n");
			groupMenu();
		} else if (choice.equalsIgnoreCase("x"))
			System.exit(0);
		else {
			try {
				List<Student> studentsWithoutGroup = new ArrayList<Student>();
				int intValue = Integer.parseInt(choice);
				StudentGroup group = entityManager.find(StudentGroup.class, intValue);
				
				System.out.println("Valley Art School Management System - " + group.getName() + "\r\n");
				if(group.getStudents() != null) {
					for (Student student : group.getStudents()) {
						System.out.println("      " + student.getId() + ")" + " " + student.getName() + "," + " "
								+ student.getBirthYear());
					}
				}
				System.out.println("b) Back to Groups\r\n" + "a) Add student to this group\r\n" + "x) Exit\r\n"
						+ "Please enter your choice:");
				String input = scanner.nextLine();
				if (input.equalsIgnoreCase("b")) {
					groupMenu();
				} else if (input.equalsIgnoreCase("a")) {
					System.out.println("Valley Art School Management System - " + "Add to " + group.getName() + "\r\n");
					List<Student> studentsAll = entityManager.createQuery("from Student", Student.class)
							.getResultList();

					for (Student student : studentsAll) {
						if (student.getGroupId() == null) {
							Student studentObj = new Student();
							studentObj.setId(student.getId());
							studentObj.setName(student.getName());
							studentObj.setBirthYear(student.getBirthYear());
							studentsWithoutGroup.add(studentObj);
						}
					}
					for (Student student : studentsWithoutGroup) {
						System.out.println("      " + student.getId() + ")" + " " + student.getName() + "," + " "
								+ student.getBirthYear());
					}
					System.out.println(
							"b) Back to " + group.getName() + "\r\n" + "x) Exit\r\n" + "Please enter your choice:");
					String val = scanner.nextLine();
					if (val.equalsIgnoreCase("b")) {
						groupMenu();
					} else if (val.equalsIgnoreCase("x")) {
						System.exit(0);
					} else {
						try {
							int choiceValue = Integer.parseInt(val);
							for (Student student : studentsWithoutGroup) {
								if (student.getId() == choiceValue) {
									Student updateStudent = new Student();
									updateStudent.setId(student.getId());
									updateStudent.setName(student.getName());
									updateStudent.setBirthYear(student.getBirthYear());
									updateStudent.setGroupId(group.getId());
									entityManager.getTransaction().begin();
									updateStudent = entityManager.merge(updateStudent);
									entityManager.getTransaction().commit();
								}
							}
							displayGroupMembers(group.getId());
						} catch (NumberFormatException e) {
							System.out.println("Invalid input");
						}
					}
				} else if (input.equalsIgnoreCase("x")) {
					System.exit(0);
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input");
			}
		}
	}
}
