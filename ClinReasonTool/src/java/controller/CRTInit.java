package controller;

import javax.faces.bean.*;

import controller.MeshImporter;
import database.HibernateUtil;

/**
 * We init here some application stuff, like hibernate,....
 * @author ingahege
 *
 */
@ManagedBean(name = "crtInit", eager = true)
@ApplicationScoped
public class CRTInit {

	
	public CRTInit(){
		HibernateUtil.initHibernate();
		//MeshImporter.main(null);
	}
}
