package org.mz.deepository.workbench;

import java.io.File;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.FileStatsStorage;

public class UI {

    public static void main(String[] args) {
        UIServer ui = UIServer.getInstance();
        File statsFile = new File(args[0]);

        ui.attach(new FileStatsStorage(statsFile));
    }
}
