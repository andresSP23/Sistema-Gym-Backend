package com.ansicode.SistemaAdministracionGym.debug;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${app.storage.comprobantes-dir:storage/comprobantes}")
    private String comprobantesDir;

    @Value("${app.storage.contratos-dir:uploads/contratos/}")
    private String contratosDir;

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> debugConfig() {
        Map<String, Object> status = new HashMap<>();
        status.put("activeProfile", activeProfile);
        status.put("comprobantesDir", comprobantesDir);
        status.put("contratosDir", contratosDir);

        status.put("comprobantesCheck", checkPath(comprobantesDir));
        status.put("contratosCheck", checkPath(contratosDir));
        status.put("volumeDataCheck", checkPath("/data"));

        return ResponseEntity.ok(status);
    }

    private Map<String, Object> checkPath(String pathStr) {
        Map<String, Object> check = new HashMap<>();
        try {
            Path path = Paths.get(pathStr);
            check.put("path", path.toAbsolutePath().toString());
            check.put("exists", Files.exists(path));
            check.put("isWritable", Files.isWritable(path));
            check.put("isDirectory", Files.isDirectory(path));

            // Try creating directory if not exists
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                    check.put("createDirAttempt", "Created successfully");
                } catch (Exception e) {
                    check.put("createDirAttempt", "Failed: " + e.getMessage());
                }
            }

            // Try writing a test file
            if (Files.exists(path)) {
                try {
                    Path testFile = path.resolve("test-write.txt");
                    Files.writeString(testFile, "Write Test OK");
                    check.put("writeTest", "Success");
                    Files.deleteIfExists(testFile);
                } catch (Exception e) {
                    check.put("writeTest", "Failed: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            check.put("error", e.getMessage());
        }
        return check;
    }
}
