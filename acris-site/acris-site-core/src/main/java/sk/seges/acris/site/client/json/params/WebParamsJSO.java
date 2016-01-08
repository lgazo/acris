package sk.seges.acris.site.client.json.params;

import sk.seges.acris.site.client.json.BaseJSONModel;
import sk.seges.acris.site.client.json.JSONModel;

import com.google.gwt.core.client.JsArray;

public class WebParamsJSO extends BaseJSONModel implements WebParams {

	private static final long serialVersionUID = -728318142595408871L;

	public WebParamsJSO(JSONModel fromJson) {
		super(fromJson);
	}

	@Override
	public Boolean isPublishOnSaveEnabled() {
		return data.getBoolean(PUBLISH_ON_SAVE_ENABLED);
	}

	@Override
	public void setPublishOnSaveEnabled(boolean publishOnSaveEnabled) {
		data.set(PUBLISH_ON_SAVE_ENABLED, publishOnSaveEnabled);
	}

	@Override
	public Boolean isOfflineAutodetectMode() {
		return data.getBoolean(OFFLINE_AUTODETECT_MODE);
	}

	@Override
	public void setOfflineAutodetectMode(boolean mode) {
		data.set(OFFLINE_AUTODETECT_MODE, mode);
	}

	@Override
	public Boolean isProductCategorySingleSelect() {
		return data.getBoolean(PRODUCT_CATEGORY_SINGLE_SELECT);
	}

	@Override
	public void setProductCategorySingleSelect(boolean productCategorySingleSelect) {
		data.set(PRODUCT_CATEGORY_SINGLE_SELECT, productCategorySingleSelect);
	}

    @Override
    public OfflineMode getOfflineMode() {
        String offlineMode = data.get(OFFLINE_MODE);
        if (offlineMode == null) {
            return null;
        }
        return OfflineMode.valueOf(offlineMode);
    }

    @Override
    public void setOfflineMode(OfflineMode offlineMode) {
        data.set(OFFLINE_MODE, offlineMode.toString());
    }
	
	@Override
	public Boolean isFiltersEnabled() {
		return data.getBoolean(PRODUCT_LIST_FILTERS_ENABLED);
	}

	@Override
	public void setFiltersEnabled(boolean filtersEnabled) {
		data.set(PRODUCT_LIST_FILTERS_ENABLED, filtersEnabled);
	}

	@Override
	public Boolean isProductListSortEnabled() {
		return data.getBoolean(PRODUCT_LIST_SORT_ENABLED);
	}

	@Override
	public void setProductListSortEnabled(boolean productListSortEnabled) {
		data.set(PRODUCT_LIST_SORT_ENABLED, productListSortEnabled);
	}

	@Override
	public String getSearchMode() {
		return data.get(SEARCH_MODE);
	}

	@Override
	public void setSearchMode(String mode) {
		data.set(SEARCH_MODE, mode);
	}

	@Override
	public Boolean isSearchLocalePrefix() {
		return data.getBoolean(SEARCH_LOCALE_PREFIX);
	}

	@Override
	public void setSearchLocalePrefix(boolean prefix) {
		data.set(SEARCH_LOCALE_PREFIX, prefix);
	}

	@Override
	public String getBluewaveUrl() {
		return data.get(BLUEWAVE_URL);
	}

	@Override
	public void setBluewaveUrl(String bluewaveUrl) {
		data.set(BLUEWAVE_URL, bluewaveUrl);
	}

	@Override
	public String getBluewaveUsername() {
		return data.get(BLUEWAVE_USERNAME);
	}

	@Override
	public void setBluewaveUsername(String bluewaveUsername) {
		data.set(BLUEWAVE_USERNAME, bluewaveUsername);
	}

	@Override
	public String getBluewavePassword() {
		return data.get(BLUEWAVE_PASSWORD);
	}

	@Override
	public void setBluewavePassword(String bluewavePassword) {
		data.set(BLUEWAVE_PASSWORD, bluewavePassword);
	}

	@Override
	public String[] getBreadcrumbItemsList() {
		return data.getStringArray(BREADCRUMB_ITEMS_LIST);
	}

	@Override
	public void setBreadcrumbItemsList(String[] breadcrumbItemsList) {
		data.set(BREADCRUMB_ITEMS_LIST, breadcrumbItemsList);
	}

	@Override
	public Boolean isProductsWithMicrositeEnabled() {
		return data.getBoolean(PRODUCTS_WITH_MICROSITE_ENABLED);
	}

	@Override
	public void setProductsWithMicrositeEnabled(boolean productsWithMicrositeEnabled) {
		data.set(PRODUCTS_WITH_MICROSITE_ENABLED, productsWithMicrositeEnabled);
		
	}

	@Override
	public Boolean isProductsWithContentEnabled() {
		return data.getBoolean(PRODUCTS_WITH_CONTENT_ENABLED);
	}

	@Override
	public void setProductsWithContentEnabled(boolean productsWithContentEnabled) {
		data.set(PRODUCTS_WITH_CONTENT_ENABLED, productsWithContentEnabled);
		
	}
	
	@Override
	public Boolean isBackgroundManagementEnabled() {
		return data.getBoolean(BACKGROUND_MANAGEMENT_ENABLED);
	}

	@Override
	public void setBackgroundManagementEnabled(boolean backgroundManagementEnabled) {
		data.set(BACKGROUND_MANAGEMENT_ENABLED, backgroundManagementEnabled);
	}

	@Override
	public boolean isIncludeProductCategoryInSearch() {
		return data.getBoolean(INCLUDE_PRODUCT_CATEGORY_IN_SEARCH);
	}

	@Override
	public void setIncludeProductCategoryInSearch(boolean includeProductCategoryInSearch) {
		data.set(INCLUDE_PRODUCT_CATEGORY_IN_SEARCH, includeProductCategoryInSearch);		
	}
	
	@Override
	public String getScrollMode() {		
		return data.get(SCROLL_MODE);
	}

	@Override
	public void setScrollMode(String scrollMode) {
		data.set(SCROLL_MODE, scrollMode);		
	}

	@Override
	public Boolean isMasterCategoryRequired() {
		return data.getBoolean(MASTER_CATEGORY_REQUIRED);
	}

	@Override
	public void setMasterCategoryRequired(boolean masterCategoryRequired) {
		data.set(MASTER_CATEGORY_REQUIRED, masterCategoryRequired);
	}

	@Override
	public String getImportURL() {
		return data.get(IMPORT_URL);
	}
	
	@Override
	public void setImportURL(String importURL) {
		data.set(IMPORT_URL, importURL);
	}
	
	@Override
	public String getImportImageURL() {
		return data.get(IMPORT_IMAGE_URL);
	}
	
	@Override
	public void setImportImageURL(String importImageURL) {
		data.set(IMPORT_IMAGE_URL, importImageURL);
	}

	@Override
	public String getRedirectLoginURL() {
		return data.get(REDIRECT_LOGIN_URL);
	}

	@Override
	public void setRedirectLoginURL(String redirectLoginURL) {
		data.set(REDIRECT_LOGIN_URL, redirectLoginURL);
	}

	@Override
	public Integer getCountOfDaysToPayInvoice() {
		return data.getInteger(COUNT_OF_DAYS_TO_PAY_INVOICE);
	}

	@Override
	public void setCountOfDaysToPayInvoice(Integer countOfDaysToPayInvoice) {
		data.set(COUNT_OF_DAYS_TO_PAY_INVOICE, countOfDaysToPayInvoice);
	}

	@Override
	public ImageSize[] getImageSizes() {
		JsArray<JSONModel> imageSizes = data.getArray(IMAGE_SIZES);
		ImageSize[] res = new ImageSize[imageSizes.length()];
		for (int i=0; i < imageSizes.length(); i++) {
			JSONModel col = imageSizes.get(i);
			res[i] = new ImageSizeJSO(col);
		}
		return res;
	}

	@Override
	public void setImageSizes(ImageSize[] imageSizes) {
		data.set(IMAGE_SIZES, JSONModel.arrayFromJson(imageSizes.toString()));
	}

	@Override
	public Boolean isNewProductSettingsEnabled() {
		return data.getBoolean(NEW_PRODUCT_SETTINGS_ENABLED);
	}

	@Override
	public void setNewProductSettingsEnabled(Boolean newProductSettingsEnabled) {
		data.set(NEW_PRODUCT_SETTINGS_ENABLED, newProductSettingsEnabled);
	}

	@Override
	public String getInvoicePrefix() {
		return data.get(INVOICE_PREFIX);
	}

	@Override
	public void setInvoicePrefix(String invoicePrefix) {
		data.set(INVOICE_PREFIX, invoicePrefix);
	}

	@Override
	public Integer getInvoiceCurrentNumber() {
		return data.getInteger(INVOICE_CURRENT_NUMBER);
	}

	@Override
	public void setInvoiceCurrentNumber(Integer invoiceCurrentNumber) {
		data.set(INVOICE_CURRENT_NUMBER, invoiceCurrentNumber);
	}

	@Override
	public Boolean getUseGeneratedVariants() {		
		return data.getBoolean(USE_GENERATED_VARIANTS);
	}

	@Override
	public void setUseGeneratedVariants(Boolean useGeneratedVariants) {
		data.set(USE_GENERATED_VARIANTS, useGeneratedVariants);
	}

	@Override
	public Boolean getEnableOrderOfNotPresentItem() {		
		return data.getBoolean(ENABLE_ORDER_OF_NOT_PRESENT_ITEM);
	}

	@Override
	public void setEnableOrderOfNotPresentItem(Boolean enableOrderOfNotPresentItem) {
		data.set(ENABLE_ORDER_OF_NOT_PRESENT_ITEM, enableOrderOfNotPresentItem);
	}
	
	@Override
	public Boolean isFastOrder() {
		return data.getBoolean(FAST_ORDER);
	}
	
	@Override
	public void setFastOrder(Boolean fastOrder) {
		data.set(FAST_ORDER, fastOrder);
	}
}
