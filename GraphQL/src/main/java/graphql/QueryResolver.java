package graphql;

import business.ModuleBusiness;
import business.UniteEnseignementBusiness;
import com.coxautodev.graphql.tools.GraphQLRootResolver;
import entities.Module;
import entities.UniteEnseignement;
import java.util.List;

public class QueryResolver implements GraphQLRootResolver {
    private final UniteEnseignementBusiness ueBusiness = new UniteEnseignementBusiness();
    private final ModuleBusiness moduleBusiness = new ModuleBusiness();

    public List<UniteEnseignement> allUEs() { return ueBusiness.getListeUE(); }
    public UniteEnseignement ueByCode(int code) { return ueBusiness.getUEByCode(code); }
    public List<UniteEnseignement> uesByDomaine(String domaine) { return ueBusiness.getUEByDomaine(domaine); }
    public List<UniteEnseignement> uesBySemestre(int semestre) { return ueBusiness.getUEBySemestre(semestre); }

    public List<Module> allModules() { return moduleBusiness.getAllModules(); }
    public Module moduleByMatricule(String matricule) { return moduleBusiness.getModuleByMatricule(matricule); }
    public List<Module> modulesByType(Module.TypeModule type) { return moduleBusiness.getModulesByType(type); }
}
