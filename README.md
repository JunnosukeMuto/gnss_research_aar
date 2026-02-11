# gnss_research_aar

## setup

create `local.properties` and write below:

```text
"sdk.dir=C\:\\Users\\{username}\\AppData\\Local\\Android\\Sdk"
```

## install

```sh
./gradlew assembleRelease
```

move `gnss_research_aar/lib/build/outputs/aar/lib-release.aar` to `UnityProject/Assets/Plugins/Android/`

## description

```cs
AndroidJavaClass ble = new("com.example.BleCentral");
ble.CallStatic("start", gameObjectName, nameof(onConnect), nameof(onGnssData))
ble.CallStatic("stop", gameObjectName, nameof(onDisconnect))
```

`lib/classes.jar` is `C:\Program Files\Unity\Hub\Editor\{version}\Editor\Data\PlaybackEngines\AndroidPlayer\Variations\mono\Release\Classes\classes.jar`
