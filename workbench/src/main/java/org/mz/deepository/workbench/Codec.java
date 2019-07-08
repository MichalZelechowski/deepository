package org.mz.deepository.workbench;

public interface Codec<SOURCE, TARGET> {

    SOURCE decode(TARGET target);

    TARGET encode(SOURCE source);

}
