{ pkgs, ... }: {
  channel = "stable-24.11";

  packages = [
    pkgs.jdk17
  ];

  env = {
    JAVA_HOME = "${pkgs.jdk17}";
  };

  idx = {
    extensions = [
      "vscjava.vscode-java-pack"
    ];

    workspace = {
      onCreate = {
        gradle-perms = "chmod +x gradlew";
        gradle-assemble = "./gradlew assembleDebug";
        default.openFiles = [
          "app/src/main/java/com/marketplace/connect/ui/MainActivity.java"
        ];
      };
    };
  };
}
