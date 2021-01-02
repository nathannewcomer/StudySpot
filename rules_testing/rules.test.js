const assert = require('assert');
const firebase = require('@firebase/testing');

const MY_PROJECT_ID = "alien-cedar-291114";


describe('testing firestore security rules', () => {
    const admin = firebase.initializeAdminApp({ projectId: MY_PROJECT_ID }).firestore();
    const db = firebase.initializeTestApp({ projectId: MY_PROJECT_ID }).firestore();
    let testDoc;

    afterAll(() => {
        return firebase.apps().map(app => app.delete());
    });

    afterEach(() => {
        return firebase.clearFirestoreData({
            projectId: MY_PROJECT_ID
        });
    });

    describe('testing studyspot collection security rules', () => {
        beforeEach(() => {
            const setupDoc = admin.collection("studyspots").doc("testDoc");
            testDoc = db.collection("studyspots").doc("testDoc");

            return setupDoc.set({
                address: "1234 road street",
                avg_light: 0,
                avg_noise: 0,
                avg_rating: 0,
                coords: new firebase.firestore.GeoPoint(0, 0),
                name: "Test Document",
                schedule: "www.google.com"
            });
        });
  
        test('can read studyspots', async () => {
            try {
                await firebase.assertSucceeds(testDoc.get());
            }
            catch (e) {
                console.log("test error \n");
                console.log(e.message);
            }
        });

        describe("test updates to studyspot averages", () => {

            test('can update light', async () => {
                await firebase.assertSucceeds(testDoc.update({
                    avg_light: 1.0
                }));
            });

            test('avg_light cannot be negative', async () => {
                await firebase.assertFails(testDoc.update({
                    avg_light: -1.0
                }));
            });

            test('avg_light must be number', async () => {
                await firebase.assertFails(testDoc.update({
                    avg_light: "hello"
                }));
            });

            test('can update noise', async () => {
                await firebase.assertSucceeds(testDoc.update({
                    avg_noise: 1.0
                }));
            });

            test('avg_noise must be number', async () => {
                await firebase.assertFails(testDoc.update({
                    avg_noise: "hello"
                }));
            });

            test('can update rating', async () => {
                await firebase.assertSucceeds(testDoc.update({
                    avg_rating: 1.0
                }));
            });

            test('avg_rating cannot be negative', async () => {
                await firebase.assertFails(testDoc.update({
                    avg_rating: -1.0
                }));
            });

            test('avg_rating must be number', async () => {
                await firebase.assertFails(testDoc.update({
                    avg_light: "hello"
                }));
            });

        });
    
        test('cant change address', async () => {
            await firebase.assertFails(testDoc.update({
                address: "test"
            }));
        });

        test('cant change name', async () => {
            await firebase.assertFails(testDoc.update({
                name: "test"
            }));
        });

        test('cant change schedule', async () => {
            await firebase.assertFails(testDoc.update({
                schedule: "test"
            }));
        });

        test('cant change coordinates', async () => {
            await firebase.assertFails(testDoc.update({
                coords: new firebase.firestore.GeoPoint(1,1)
            }));
        });

        test('cant add new fields', async () => {
            await firebase.assertFails(testDoc.update({
                extra: true
            }));
        });
    
    });

    describe("testing reviews security rules", () => {
        beforeEach(() => {
            testDoc = db.collection("studyspots").doc("test").collection("reviews").doc("testDoc");
        });

        test('can create reviews', () => {
            firebase.assertSucceeds(testDoc.set({
                comment: "hello",
                likes: 2,
                rating: 3.5,
                photoPath: "www.google.com",
                spotName: "test"
            }));
        });

        test('cant create review w/o comment', () => {
            firebase.assertFails(testDoc.set({
                likes: 2,
                rating: 3.5,
                photoPath: "www.google.com",
                spotName: "test"
            }));
        });

        test('cant create review w/o likes', () => {
            firebase.assertFails(testDoc.set({
                comment: "hello",
                rating: 3.5,
                photoPath: "www.google.com",
                spotName: "test"
            }));
        });

        test('cant create review w/o rating', () => {
            firebase.assertFails(testDoc.set({
                comment: "hello",
                likes: 2,
                photoPath: "www.google.com",
                spotName: "test"
            }));
        });

        test('cant create review w/o photoPath', () => {
            firebase.assertFails(testDoc.set({
                comment: "hello",
                rating: 3.5,
                likes: 2,
                spotName: "test"
            }));
        });

        test('cant create review w/o spotName', () => {
            firebase.assertFails(testDoc.set({
                comment: "hello",
                likes: 2,
                rating: 3.5,
                photoPath: "www.google.com",
            }));
        });

        test('cant create review with negative likes', () => {
            firebase.assertFails(testDoc.set({
                comment: "hello",
                likes: -2,
                rating: 3.5,
                photoPath: "www.google.com",
                spotName: "test"
            }));
        });

        test('cant create review with negative rating', () => {
            firebase.assertFails(testDoc.set({
                comment: "hello",
                likes: 2,
                rating: -3.5,
                photoPath: "www.google.com",
                spotName: "test"
            }));
        });

        test('can read reviews', async () => {
            const setupDoc = admin.collection("studyspots").doc("test").collection("reviews").doc("testDoc");
            await setupDoc.set({
                comment: "hello",
                likes: 2,
                rating: 3.5,
                photoPath: "www.google.com",
                spotName: "test"
            });
            return firebase.assertSucceeds(testDoc.get());
        });

    });

    describe('testing light_record security rules', () => {
        beforeEach(() => {
            const setupDoc = admin.doc("studyspots/test/light_record/singlerecord");
            //need to use . notation to avoid overwriting entire map during updates
            testDoc = db.collection("studyspots").doc("test").collection("light_record").doc("singlerecord");
            return setupDoc.set({
                light_record: {}
            });
        });

        test("can read light_record", () => {
            return firebase.assertSucceeds(testDoc.get());
        });
        
        test("can create empty light_record", () => {
            testDoc = db.doc("studyspots/test/light_record/singlerecord2");
            return firebase.assertSucceeds(testDoc.set({
                light_record: {

                }
            }));
        });

        test("can create light_record", () => {
            testDoc = db.doc("studyspots/test/light_record/singlerecord2");
            return firebase.assertSucceeds(testDoc.set({
                light_record: {
                    1: "value",
                    2: "value2"
                }
            }));
        });

        test("can update light_record", () => {
            return firebase.assertSucceeds(testDoc.update({
                light_record: {
                    1: "value"
                }
            }));
        });

        test('cant add new fields', async () => {
            await firebase.assertFails(testDoc.update({
                extra: true
            }));
        });
    });

    describe('testing noise_record security rules', () => {

        beforeEach(() => {
            const setupDoc = admin.doc("studyspots/test/noise_record/singlerecord");
            //need to use . notation to avoid overwriting entire map during updates
            testDoc = db.collection("studyspots").doc("test").collection("noise_record").doc("singlerecord");
            return setupDoc.set({
                noise_record: {}
            });
        });

        test("can read noise_record", () => {
            return firebase.assertSucceeds(testDoc.get());
        });

        test("can create empty noise_record", () => {
            testDoc = db.doc("studyspots/test/noise_record/singlerecord2");
            return firebase.assertSucceeds(testDoc.set({
                noise_record: {

                }
            }));
        });

        test("can create noise_record", () => {
            testDoc = db.doc("studyspots/test/noise_record/singlerecord2");
            return firebase.assertSucceeds(testDoc.set({
                noise_record: {
                    1: "value",
                    2: "value2"
                }
            }));
        });

        test("can update noise_record", () => {
            return firebase.assertSucceeds(testDoc.update({
                noise_record: {
                    1: "value"
                }
            }));
        });

        test('cant add new fields', async () => {
            await firebase.assertFails(testDoc.update({
                extra: true
            }));
        });


    });


});
