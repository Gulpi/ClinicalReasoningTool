package controller;

import javax.faces.bean.*;
import java.io.Serializable;

@ManagedBean(name = "jsfTest", eager = true)
@SessionScoped
public class JSFTest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name="hallo2";

	public JSFTest(){
		System.out.println("hallo");
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
