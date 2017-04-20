package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.queries.SectionsView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class LocalRiversService implements RiversApi {
    private final Database mDatabase;

    private LocalRiversService(Database database) {
        mDatabase = checkNotNull(database);
    }

    @NonNull
    @Override
    public Observable<Image> createImage(@NonNull Image.Builder builder) {
        String id = UUID.randomUUID().toString();

        try {
            Image image = builder.id(id).build();

            Document document = mDatabase.getDocument(id);
            document.putProperties(image.getProperties());

            return Observable.just(image);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Section> createSection(@NonNull Section.Builder builder) {
        String id = UUID.randomUUID().toString();

        try {
            Section section = builder.id(id).build();

            Document document = mDatabase.getDocument(id);
            document.putProperties(section.getProperties());

            return Observable.just(section);
        } catch (CouchbaseLiteException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Void> deleteSection(@NonNull Section section) {
        Document document = mDatabase.getDocument(section.getId());

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
        Document document = mDatabase.getDocument(id);

        if (document != null) {
            Image image = new Image.Builder(document).build();
            return Observable.just(image);
        } else {
            return Observable.empty();
        }
    }

    @NonNull
    @Override
    public Observable<List<Section>> streamSections() {
        View view = SectionsView.getInstance(mDatabase);
        LiveQuery query = view.createQuery().toLiveQuery();

        QueryObserver observer = new QueryObserver<List<Section>>() {
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
                    mSections.add(new Section.Builder(row.getDocument()).build());
                }

                for (Subscriber subscriber : mSubscribers) {
                    subscriber.onNext(mSections);
                }
            }
        };

        query.addChangeListener(observer);
        query.start();

        return Observable.create(observer);
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