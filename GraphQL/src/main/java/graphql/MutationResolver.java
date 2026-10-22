package graphql;

import business.ModuleBusiness;
import business.UniteEnseignementBusiness;
import com.coxautodev.graphql.tools.GraphQLRootResolver;
import entities.Module;
import entities.UniteEnseignement;

public class MutationResolver implements GraphQLRootResolver {
    private final UniteEnseignementBusiness ueBusiness = new UniteEnseignementBusiness();
    private final ModuleBusiness moduleBusiness = new ModuleBusiness();

    public boolean addUniteEnseignement(int code, String domaine, String responsable, int credits, int semestre) {
        if (ueBusiness.getUEByCode(code) != null) return false;
        return ueBusiness.addUniteEnseignement(new UniteEnseignement(code, domaine, responsable, credits, semestre));
    }

    public boolean updateUniteEnseignement(int code, String domaine, String responsable, int credits, int semestre) {
        if (ueBusiness.getUEByCode(code) == null) return false;
        return ueBusiness.updateUniteEnseignement(code, new UniteEnseignement(code, domaine, responsable, credits, semestre));
    }

    public boolean deleteUniteEnseignement(int code) {
        UniteEnseignement ue = ueBusiness.getUEByCode(code);
        if (ue == null || !moduleBusiness.getModulesByUE(ue).isEmpty()) return false;
        return ueBusiness.deleteUniteEnseignement(code);
    }

    public boolean addModule(String matricule, String nom, int coefficient, int volumeHoraire, Module.TypeModule type, int codeUE) {
        if (moduleBusiness.getModuleByMatricule(matricule) != null) return false;
        UniteEnseignement ue = ueBusiness.getUEByCode(codeUE);
        if (ue == null) return false;
        return moduleBusiness.addModule(new Module(matricule, nom, coefficient, volumeHoraire, type, ue));
    }

    public Module updateModule(String matricule, String nom, int coefficient, int volumeHoraire, Module.TypeModule type, int codeUE) {
        Module current = moduleBusiness.getModuleByMatricule(matricule);
        UniteEnseignement ue = ueBusiness.getUEByCode(codeUE);
        if (current == null || ue == null) return null;
        Module updated = new Module(matricule, nom, coefficient, volumeHoraire, type, ue);
        return moduleBusiness.updateModule(matricule, updated) ? updated : null;
    }

    public boolean deleteModule(String matricule) { return moduleBusiness.deleteModule(matricule); }
}
