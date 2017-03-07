package test;


public class MyException extends RuntimeException {	
	private static final long serialVersionUID = 1L;
	String error;
	
	public MyException(){
		super();
	}
	
	public MyException(Exception e){
		error=e.toString();
	}
	
	public String getError() {
		return error;
	}
	
	
}
