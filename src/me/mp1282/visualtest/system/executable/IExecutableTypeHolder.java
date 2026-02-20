package me.mp1282.visualtest.system.executable;

import java.util.Optional;

public interface IExecutableTypeHolder<T extends Executable> {

    Optional<T> findExecutableByPersistenceId(String persistenceId);
    String getType();
}
