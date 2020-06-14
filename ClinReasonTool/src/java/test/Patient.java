package test;
public class Patient {
	public static final int GENDER_M = 1; //male
	public static final int GENDER_F = 2; //female
	public static final int GENDER_U = -1; //unknown, both, it is complicated etc.
	
	private int age = -1;
	private int gender = -1;
	
	public int getAge() {return age;}
	public void setAge(int age) {this.age = age;}
	public int getGender() {return gender;}
	public void setGender(int gender) {this.gender = gender;}	
	
}
