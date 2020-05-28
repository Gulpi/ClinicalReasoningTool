package util;



public class Helper {
	static String file = "/Users/ingahege/ownCloud/documents/Inga/marie_curie/WP3/StudieKrakow/session_ids.txt";

	public static void main(String[] args ){
		
		//String todecrypt = "e8574a4466092e0d";
		//String decryptedStr = Encoder.getInstance().decodeQueryParam(todecrypt);
		//System.out.println(decryptedStr);
		
		/*try{
			LineNumberReader lbr = new LineNumberReader(new FileReader(file));
			String line;
			while((line=lbr.readLine())!=null){
				String s = "";
				try{
					s = Encoder.getInstance().decodeQueryParam(line);
					System.out.println(s);
				}
				catch(Throwable e){
					System.out.println("err");
				}
				
			}
		}
		catch(Throwable e){}*/
		String toencrypt = "205914";
		String encryptedStr = Encoder.getInstance().encodeQueryParam(toencrypt);
		System.out.println(encryptedStr);
		
	}
}
