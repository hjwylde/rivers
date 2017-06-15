package com.hjwylde.rivers.db.services;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.hjwylde.rivers.db.models.ImageDocument;
import com.hjwylde.rivers.db.models.SectionDocument;
import com.hjwylde.rivers.db.util.SectionQuery;
import com.hjwylde.rivers.db.views.SectionsView;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;

import static java.util.Objects.requireNonNull;

public final class LocalRiversService implements RiversApi {
    @NonNull
    private final Database mDatabase;

    private LocalRiversService(@NonNull Database database) {
        mDatabase = requireNonNull(database);
    }

    @NonNull
    @Override
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        ImageDocument.Builder documentBuilder = ImageDocument.builder(mDatabase).copy(builder);

        try {
            ImageDocument imageDocument = documentBuilder.build();

            return Single.just(imageDocument);
        } catch (CouchbaseLiteException e) {
            return Single.error(e);
        }
    }

    @NonNull
    @Override
    public Single<Section> createSection(@NonNull Section.Builder builder) {
        SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

        try {
            SectionDocument sectionDocument = documentBuilder.build();

            return Single.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Single.error(e);
        }
    }

    @NonNull
    @Override
    public Completable deleteSection(@NonNull Section section) {
        Document document = mDatabase.getExistingDocument(section.getId());

        try {
            if (document != null) {
                document.delete();
            }

            return Completable.complete();
        } catch (CouchbaseLiteException e) {
            return Completable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Section> findSection(@NonNull String query) {
        SectionQuery sectionQuery = new SectionQuery(query);
        View view = SectionsView.getInstance(mDatabase);

        return Observable
                .<Document>create(emitter -> {
                    try {
                        QueryEnumerator result = view.createQuery().run();

                        for (QueryRow row : result) {
                            emitter.onNext(row.getDocument());
                        }

                        emitter.onComplete();
                    } catch (CouchbaseLiteException e) {
                        emitter.onError(e);
                    }
                })
                .<Section>map(SectionDocument::new)
                .filter(sectionQuery::test);
    }

    @NonNull
    @Override
    public Maybe<Image> getImage(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            ImageDocument imageDocument = new ImageDocument(document);

            return Maybe.just(imageDocument);
        } else {
            return Maybe.empty();
        }
    }

    @NonNull
    @Override
    public Maybe<Section> getSection(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            SectionDocument sectionDocument = new SectionDocument(document);

            return Maybe.just(sectionDocument);
        } else {
            return Maybe.empty();
        }
    }

    @NonNull
    @Override
    public Observable<List<Section>> streamSections() {
        // TODO (hjw): this has potential to use a lot of memory
        View view = SectionsView.getInstance(mDatabase);
        LiveQuery query = view.createQuery().toLiveQuery();

        QueryObserver<List<Section>> observer = new QueryObserver<List<Section>>() {
            private List<ObservableEmitter<List<Section>>> mSubscribers = new ArrayList<>();

            private List<Section> mSections = new ArrayList<>();

            @Override
            public void changed(LiveQuery.ChangeEvent event) {
                mSections = new ArrayList<>();
                for (QueryRow row : event.getRows()) {
                    mSections.add(new SectionDocument(row.getDocument()));
                }

                for (ObservableEmitter<List<Section>> subscriber : mSubscribers) {
                    subscriber.onNext(mSections);
                }
            }

            @Override
            public void subscribe(ObservableEmitter<List<Section>> subscriber) throws Exception {
                mSubscribers.add(subscriber);

                if (!mSections.isEmpty()) {
                    subscriber.onNext(mSections);
                }
            }
        };

        query.addChangeListener(observer);
        query.start();

        return Observable.create(observer);
    }

    @NonNull
    @Override
    public Single<Section> updateSection(@NonNull Section.Builder builder) {
        SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

        try {
            SectionDocument sectionDocument = documentBuilder.build();

            return Single.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Single.error(e);
        }
    }

    private interface QueryObserver<T> extends LiveQuery.ChangeListener, ObservableOnSubscribe<T> {
    }

    public static final class Builder {
        private Database mDatabase;

        public RiversApi build() {
            return new LocalRiversService(mDatabase);
        }

        public LocalRiversService.Builder database(Database database) {
            mDatabase = database;

            return this;
        }
    }
}