package view;

public class Test3 {

	public static void main(String[] args){
		String string1 = "<html> PUTAINGA \n<br><html>";
		string1 = string1.replaceAll("<html>", "");
		string1 = string1.replaceAll("\n", "");
		string1 = string1.replaceAll("<br>", "");
		string1 = string1.replaceAll("</html>", "");
		System.out.println(string1);
		}
}
