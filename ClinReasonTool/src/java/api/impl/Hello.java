package api.impl;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.ApiInterface;
import net.casus.util.String2HashKey;

/**
 * Sample ApiInterface implemenation for demo
 *
 * @author Gulpi (=Martin Adler)
 */
public class Hello implements ApiInterface {

	@Override
	public String handle() {
		String id = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("type");
		
		String result = "";
		String2HashKey bean = new String2HashKey("id",id);
		try {
			result = new ObjectMapper().writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
