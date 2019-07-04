package com.application.material.bookmarkswallet.app.presenter;

import com.application.material.bookmarkswallet.app.data.models.BookmarkMetadata;
import com.application.material.bookmarkswallet.app.data.remote.FaviconFinderRetrofitService;
import com.application.material.bookmarkswallet.app.data.remote.TagsByUrlRetrofitService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AddBookmarkResultPresenter {
    private final AddBookmarkResultView view;
    private final FaviconFinderRetrofitService faviconFinederService;
    private final TagsByUrlRetrofitService tagsByUrlService;

    public AddBookmarkResultPresenter(AddBookmarkResultView v, FaviconFinderRetrofitService service,
                                      TagsByUrlRetrofitService service2) {
        this.view = v;
        this.faviconFinederService = service;
        this.tagsByUrlService = service2;
    }

    /**
     * retrieve icon from gallery or url
     */
    private void retrieveIcon() {
//        refreshLayout.setRefreshing(true);
//        https://besticon-demo.herokuapp.com/icon?url=github.com&size=80
    }

    /**
     *
     */
    private void retrieveTitle() {
//        if (title != null && title.compareTo("") == 0) {
    }

    public void retrieveIconAndTitle(String url, String title) {
        //TODO add unsubscribe
        Disposable subscription = tagsByUrlService.getTagsByUrl(url)
                .observeOn(Schedulers.computation())
                .map(res -> new Gson().fromJson(tableToJson(res.string()), BookmarkMetadata.class))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .flatMap(metadata -> {
                    if (metadata.getImage() == null) {
                        return
                    }
                    return Observable.just(metadata);
                })
                .map(metadata -> {
                    if (metadata.getImage() == null) {

                    }
                    return metadata;
                })
                .subscribe(bookmarkMetadata -> {
                            view.onRetrieveTitleSuccess(bookmarkMetadata.getSiteName());
                            view.onRetrieveIconSuccess(bookmarkMetadata.getImage());
                        },
                        error -> view.onRetrieveIconFailure(error.getMessage()));
    }

    //move to utils

    /**
     *
     * @param source
     * @return
     * @throws JSONException
     */
    public String tableToJson(String source) throws JSONException {
        Document doc = Jsoup.parse(source);
        JSONObject jsonObject = new JSONObject();
        //JSONArray list = new JSONArray();
        for (Element table : doc.select("table")) {
            for (Element row : table.select("tr")) {
                Elements tds = row.select("td");
                if (tds.size() > 0) {
                    String key = row.select("td").get(0).text().replace("og:", "").replace("fb:", "");
                    String value = row.select("td").get(1).text();
                    jsonObject.put(key, value);
                }
            }
        }
        return jsonObject.toString();
    }
}
