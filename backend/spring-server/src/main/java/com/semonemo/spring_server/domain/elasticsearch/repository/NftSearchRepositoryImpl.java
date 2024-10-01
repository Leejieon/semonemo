package com.semonemo.spring_server.domain.elasticsearch.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import com.semonemo.spring_server.domain.asset.model.AssetSell;
import com.semonemo.spring_server.domain.elasticsearch.document.AssetSellDocument;
import com.semonemo.spring_server.domain.elasticsearch.document.NFTSellDocument;
import com.semonemo.spring_server.global.common.CursorResult;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NftSearchRepositoryImpl implements NftSearchRepositoryCustom {

	private final ElasticsearchOperations elasticsearchOperations;

	public Page<NFTSellDocument> keywordAndOrderby(String keyword, String orderBy, int page, int size) {
		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder()
			.must(new NestedQuery.Builder()
				.path("tags")
				.query(new MatchQuery.Builder()
					.field("tags.name.ngram")
					.query(keyword)
					.operator(Operator.And)
					.build()._toQuery())
				.build()._toQuery());

		String option = "";
		SortOrder sortOrder = SortOrder.Desc;
		switch (orderBy) {
			case "high":
				option = "price";
				break;
			case "like":
				option = "likeCount";
				break;
			case "latest":
				option="createdAt";
				break;
			case "oldest":
				option="createdAt";
				sortOrder = SortOrder.Asc;
				break;
			case "low":
				option="price";
				sortOrder = SortOrder.Asc;
				break;

		}
		String finalOption = option;
		SortOrder finalSortOrder = sortOrder;
		NativeQuery query = NativeQuery.builder()
			.withQuery(boolQueryBuilder.build()._toQuery())
			.withSort(sort -> sort.field(f -> f.field(finalOption).order(finalSortOrder)))
			.withPageable(PageRequest.of(page, size))
			.build();

		SearchHits<NFTSellDocument> searchHits = elasticsearchOperations.search(query, NFTSellDocument.class);

		List<NFTSellDocument> content = searchHits.getSearchHits().stream()
			.map(SearchHit::getContent)
			.collect(Collectors.toList());

		long totalHits = searchHits.getTotalHits();
		return new PageImpl<>(content, PageRequest.of(page, size), totalHits);
	}

	@Override
	public void updateData(Long assetSellId, String type, AssetSell assetSell) {
		Long data = 0L;
		switch (type) {
			case "like":
				data = assetSell.getLikeCount();
				type = "likeCount";
				break;
			case "dislike":
				data = assetSell.getLikeCount()-1;
				type = "likeCount";
				break;
			case "purchase":
				data = assetSell.getPurchaseCount();
				type = "purchaseCount";
				break;
			case "price":
				data = assetSell.getPrice();
				type = "price";
				break;
			case "hits":
				data = assetSell.getHits();
		}
		UpdateQuery updateQuery = UpdateQuery.builder(assetSellId.toString())
			.withDocument(Document.create().append(type, data))
			.build();

		try {
			elasticsearchOperations.update(updateQuery, IndexCoordinates.of("asset_sells"));
		} catch (Exception e) {
			// 로깅 또는 예외 처리
			throw new RuntimeException("Failed to update assetSell data: " + assetSellId, e);
		}
	}
}