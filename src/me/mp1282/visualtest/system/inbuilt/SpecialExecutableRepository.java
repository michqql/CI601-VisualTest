package me.mp1282.visualtest.system.inbuilt;

import me.mp1282.visualtest.system.executable.IExecutableTypeHolder;
import me.mp1282.visualtest.system.inbuilt.special.BranchExecutable;
import me.mp1282.visualtest.system.inbuilt.special.ConstantExecutable;
import me.mp1282.visualtest.system.inbuilt.special.RangeCheckExecutable;
import me.mp1282.visualtest.system.inbuilt.special.ForLoopExecutable;
import me.mp1282.visualtest.system.inbuilt.special.MergeExecutable;
import me.mp1282.visualtest.system.inbuilt.special.SwitchExecutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpecialExecutableRepository implements IExecutableTypeHolder<SpecialExecutable> {

    private final List<SpecialExecutable> repository;

    public SpecialExecutableRepository() {
        this.repository = new ArrayList<>();
        repository.add(new ConstantExecutable());
        repository.add(new RangeCheckExecutable());
        repository.add(new BranchExecutable());
        repository.add(new SwitchExecutable());
        repository.add(new ForLoopExecutable());
        repository.add(new MergeExecutable());

        /* Initialize all special executables */
        for (SpecialExecutable executable : repository) {
            executable.init(this);
        }
    }

    public List<SpecialExecutable> getRepository() {
        return Collections.unmodifiableList(repository);
    }

    @Override
    public Optional<SpecialExecutable> findExecutableByPersistenceId(String persistenceId) {
        for (SpecialExecutable executable : repository) {
            if (executable.getPersistenceId().equals(persistenceId))
                return Optional.of(executable);
        }

        return Optional.empty();
    }

    @Override
    public String getType() {
        return "inbuilt-special";
    }
}
