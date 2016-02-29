# ngtable-spring-pageable
Spring pageable for ngTable sorting and filtering. NgTablePageable extends PageRequest and works with Spring Data Repositories (page, count and sort). Filter opterations can be implemented by building custom Spring Data Specifications.

## Usage
Add the ngTablePageableResolver to the context config of your servlet (e.g. webmvc-config.xml)
```XML
<mvc:annotation-driven  conversion-service="applicationConversionService">
    <!-- CUSTOM RESOLVERS -->
    <mvc:argument-resolvers >
        <bean id="ngTablePageableResolver" class="com.lechneralexander.spring.utils.angularjs.NgTablePageableResolver" />
    </mvc:argument-resolvers>
</mvc:annotation-driven>
```

Now NgTablePageable can be used as controller method argument
```Java
@RequestMapping(method = RequestMethod.GET, produces = "application/json")
public ResponseEntity<String> listAll(NgTablePageable pageRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json; charset=utf-8");
    
    //Get data by page request
    Page<Product> page = productRepository.findAll(pageRequest);
    //Format result
    String jsonString = NgTableUtils.toTableData(Product.toJsonArray(page.getContent()), page.getTotalElements());
    //Send Result
    return new ResponseEntity<String>(jsonString, headers, HttpStatus.OK);
}
```

## Building custom filters
Paging and sorting are handled automatically by NgTablePageable. Custom filters have to be implemented. This can be done using a Spring Data Specification.
```Java
public Page<Product> findAll(NgTablePageable pageRequest) {
    productRepository.findAll(Specifications.where(applyFilter(pageable)), pageable);
}
```

### Example of tag search filter. 
```Java
public static Specification<Product> applyTagFilterByFieldName(final NgTablePageable pageable, final String fieldName) {
	return new Specification<Product>() {
		public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
			Predicate predicate = builder.conjunction();
      
			HashMap<String, String[]> filters = pageable.getFilters();
      
            //Find filter parameter
			for (String parameter : filters.keySet()){
				if (fieldName.equals(parameter)){
					String[] selectedIds = filters.get(parameter);
					for (String selectedId : selectedIds){
						Path<Object> path = root.join(fieldName).get("id");
						predicate.getExpressions().add(builder
								.and(builder.
										equal(path, selectedId)));
					}
				}
			}

			return predicate;
		}
	};
}
```

Example: Use this filter to select all products which are tagged with all the given tags
```
URL: https://mydomain.com/restAPI/products?page=1&count=10&sorting[price]=desc&filter[tags]=10001&filter[tags]=10002
```

```Java
productRepository.findAll(Specifications.where(applyTagFilterByFieldName(pageable, "tags")), pageable);
```

### Example of text search filter

```Java
public static Specification<Product> applyTextSearchFilterByFieldName(final NgTablePageable pageable, final String fieldName) {
	return new Specification<Product>() {
		public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
			Predicate predicate = builder.conjunction();
      
			HashMap<String, String[]> filters = pageable.getFilters();
      
            //Find filter parameter
			for (String parameter : filters.keySet()){
				if (fieldName.equals(parameter)){
					String matchTerm = "%" + filters.get(parameter) + "%";
						predicate.getExpressions().add(builder
								.and(builder.
										like(builder.lower(root.get(parameter).as(String.class)), matchTerm)));
				}
			}

			return predicate;
		}
	};
}
```

Example: Use this filter to select all products which contain a certain string in their name.
```
URL: https://mydomain.com/restAPI/products?page=1&count=10&sorting[price]=desc&filter[name]=sneaker
```

```Java
productRepository.findAll(Specifications.where(applyTextSearchFilterByFieldName(pageable, "name")), pageable);
```

## License
The MIT License (MIT)

Copyright (c) 2016 Lechner Alexander

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
