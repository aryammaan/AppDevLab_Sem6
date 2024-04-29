import React, { useEffect, useState, useRef } from 'react';
import { Button, ToastAndroid, StatusBar, StyleSheet, Text, View, SafeAreaView, Image, Pressable } from 'react-native';
import { Camera } from 'expo-camera';
import { shareAsync } from 'expo-sharing';
import * as MediaLibrary from 'expo-media-library';

const App = () => {
    const [hasCameraPermission, setHasCameraPermission] = useState();
    const [hasMediadLibraryPermission, setHasMediadLibraryPermission] = useState();
    const [photo, setPhoto] = useState();
    const cameraRef = useRef();
    
    const setToastMsg = msg => {
        ToastAndroid.showWithGravity(msg, ToastAndroid.SHORT, ToastAndroid.CENTER);
    }

    useEffect(() => {
        (async () => {
            const cameraPermission = await Camera.requestCameraPermissionsAsync();
            const mediaLibraryPermission = await MediaLibrary.requestPermissionsAsync();
            
            setHasCameraPermission(cameraPermission.status === 'granted');
            setHasMediadLibraryPermission(mediaLibraryPermission.status === 'granted');
        
        })();
    
    }, []);

    if (hasCameraPermission === undefined) {
        return <Text>Requesting Permissions...</Text>;
    }else if (!hasCameraPermission) {
        return <Text>Permissions for camera not granted. Please change this in settings.</Text>;
    }

    const takePic = async () => {
        const options = {
            quality: 1,
            base64: true,
            exif: false,
        };

        const newPhoto = await cameraRef.current.takePictureAsync(options);
        setPhoto(newPhoto);
    }

    if (photo) {
        const sharePic = () => {
            shareAsync(photo.uri).then(() => {
                setPhoto(undefined);
            });
        }

        const savePhoto = () => {
            MediaLibrary.saveToLibraryAsync(photo.uri).then(() => {
                setPhoto(undefined);
            });
            setToastMsg('Image saved');
        }

        return (
            <SafeAreaView style={styles.saveContainer}>
                <Image
                    source={{ uri: 'data:image/jpg;base64,' + photo.base64 }}
                    style={styles.preview}
                />
                <View style={styles.shareButtonContainer}>
                    <Button
                        title='Share'
                        onPress={sharePic}
                    />
                    <View style={styles.saveButtonContainer}>
                        {hasMediadLibraryPermission ?
                            <Button
                                title='save'
                                onPress={savePhoto}
                            />
                            :
                            undefined
                        }
                    </View>
                    <Button
                        title='Discard'
                        onPress={() => setPhoto(undefined)}
                    />
                </View>
            </SafeAreaView>
        );
    }

    return (
        <Camera style={styles.container} ref={cameraRef} ratio='16:9'>
            <View style={styles.buttonContainer}>
            <Pressable style={styles.shootPhoto} onPress={takePic}>
                <View />
            </Pressable>
            </View>
            <StatusBar />
        </Camera>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'flex-end',
    },
    buttonContainer: {
        backgroundColor: '#FFF',
        marginBottom:10,
        borderRadius:40,
    },
    preview: {
        width: '98%',
        height: 700,
    },
    saveContainer: {
        alignItems: 'center',
        justifyContent: 'flex-end',
        marginTop:10,
    },
    shareButtonContainer: {
        flexDirection: 'row', 
        marginTop: 10,
    },
    saveButtonContainer: {
        marginHorizontal: 10,
    },
    shootPhoto: {
        padding:40, 
        borderWidth:1, 
        borderRadius:40,
    },
});

export default App;
