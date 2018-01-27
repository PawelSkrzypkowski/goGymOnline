package pl.pawelskrzypkowski.application;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHolder {
    private JPAHolder(){ }

    private static class Holder {
        private static final EntityManagerFactory INSTANCE = Persistence.createEntityManagerFactory("myDatabase");
    }

    public static EntityManager getEntityManager() {
        return Holder.INSTANCE.createEntityManager();
    }
}