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
import com.hjwylde.rivers.db.views.SectionsView;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.util.SectionQuery;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static java.util.Objects.requireNonNull;

public final class LocalRiversService implements RiversApi {
    @NonNull
    private final Database mDatabase;

    private LocalRiversService(@NonNull Database database) {
        mDatabase = requireNonNull(database);
    }

    @NonNull
    @Override
    public Observable<Image> createImage(@NonNull Image.Builder builder) {
        ImageDocument.Builder documentBuilder = ImageDocument.builder(mDatabase).copy(builder);

        try {
            ImageDocument imageDocument = documentBuilder.build();

            return Observable.just(imageDocument);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Section> createSection(@NonNull Section.Builder builder) {
        SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

        try {
            SectionDocument sectionDocument = documentBuilder.build();

            return Observable.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Void> deleteSection(@NonNull Section section) {
        Document document = mDatabase.getExistingDocument(section.getId());

        try {
            if (document != null) {
                document.delete();
            }
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }

        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<Image> getImage(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            ImageDocument imageDocument = new ImageDocument(document);

            return Observable.just(imageDocument);
        } else {
            return Observable.empty();
        }
    }

    @NonNull
    @Override
    public Observable<Section> getSection(@NonNull String id) {
        Document document = mDatabase.getExistingDocument(id);

        if (document != null) {
            SectionDocument sectionDocument = new SectionDocument(document);

            return Observable.just(sectionDocument);
        } else {
            return Observable.empty();
        }
    }

    @NonNull
    @Override
    public Observable<List<Section>> searchSections(@NonNull String query) {
        // TODO (hjw): this has potential to use a lot of memory
        SectionQuery sectionQuery = new SectionQuery(query);
        View view = SectionsView.getInstance(mDatabase);

        List<Section> sections = new ArrayList<>();

        try {
            QueryEnumerator result = view.createQuery().run();

            for (QueryRow row : result) {
                SectionDocument sectionDocument = new SectionDocument(row.getDocument());

                if (sectionQuery.test(sectionDocument)) {
                    sections.add(sectionDocument);
                }
            }
        } catch (CouchbaseLiteException e) {
            Observable.error(e);
        }

        return Observable.just(sections);
    }

    @NonNull
    @Override
    public Observable<List<Section>> streamSections() {
        // TODO (hjw): this has potential to use a lot of memory
        View view = SectionsView.getInstance(mDatabase);
        LiveQuery query = view.createQuery().toLiveQuery();

        QueryObserver<List<Section>> observer = new QueryObserver<List<Section>>() {
            private List<Subscriber<? super List<Section>>> mSubscribers = new ArrayList<>();

            private List<Section> mSections = new ArrayList<>();

            @Override
            public void call(Subscriber<? super List<Section>> subscriber) {
                mSubscribers.add(subscriber);

                if (!mSections.isEmpty()) {
                    subscriber.onNext(mSections);
                }
            }

            @Override
            public void changed(LiveQuery.ChangeEvent event) {
                mSections = new ArrayList<>();
                for (QueryRow row : event.getRows()) {
                    mSections.add(new SectionDocument(row.getDocument()));
                }

                for (Subscriber<? super List<Section>> subscriber : mSubscribers) {
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
    public Observable<Section> updateSection(@NonNull Section.Builder builder) {
        SectionDocument.Builder documentBuilder = SectionDocument.builder(mDatabase).copy(builder);

        try {
            SectionDocument sectionDocument = documentBuilder.build();

            return Observable.just(sectionDocument);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    private interface QueryObserver<T> extends LiveQuery.ChangeListener, Observable.OnSubscribe<T> {
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