package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileTreeImpl implements FileTree {
    public Optional<String> tree(Path path) {
        File file = new File(String.valueOf(path));
        if (!file.exists()) return Optional.empty();
        if (file.isFile()) {
            return Optional.of(file.getName() + " " + file.length() + " bytes");
        }
        if (file.isDirectory()) {

            return Optional.of(directoryTree(file, new ArrayList<>()));
        }
        return Optional.empty();
    }

    private String directoryTree(File folder, List<Boolean> lastFolders) {
        StringBuilder directory = new StringBuilder();
        if (!lastFolders.isEmpty())
            directory.append(!(lastFolders.get(lastFolders.size() - 1)) ? "├─ " : "└─ ");
        directory.append(folder.getName()).append(" ").append(folderSize(folder));

        File[] files = folder.listFiles();
        assert files != null;
        int count = files.length;
        files = sortFiles(files);
        for (int i = 0; i < count; i++) {
            directory.append("\n");
            for (Boolean lastFolder : lastFolders) {
                if (lastFolder) {
                    directory.append("   ");
                } else {
                    directory.append("│  ");
                }
            }
            if (files[i].isFile()) {
                directory.append(i + 1 == count ? "└" : "├").append("─ ").append(files[i].getName()).append(" ").append(files[i].length()).append(" bytes");
            } else {
                ArrayList<Boolean> list = new ArrayList<>(lastFolders);
                list.add(i + 1 == count);
                directory.append(directoryTree(files[i], list));
            }
        }
        return directory.toString();
    }

    private long getFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();

        assert files != null;

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            } else {
                size += getFolderSize(file);
            }
        }
        return size;
    }

    private String folderSize(File folder) {
        return getFolderSize(folder) + " bytes";
    }

    private File[] sortFiles(File[] folder) {
        List<File> file = new ArrayList<>();
        List<File> directory = new ArrayList<>();
        for (File item : folder) {
            if (item.isDirectory())
                file.add(item);
        }
        file.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        for (File value : folder) {
            if (value.isFile())
                directory.add(value);
        }
        directory.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        file.addAll(directory);

        return file.toArray(new File[0]);
    }
}