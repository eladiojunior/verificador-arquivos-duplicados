package br.com.devd2.utilitario.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Getter;

public class JpaUtil {
    @Getter
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("file-index-pu");
}
