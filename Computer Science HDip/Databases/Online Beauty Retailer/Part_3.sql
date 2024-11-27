-- Part 3

-- #########################################################################################

-- 1) Show all the products along with the supplier detail who supplied the products

SELECT products.product_id, products.product_description, suppliers.supplier_id, suppliers.supplier_name
FROM products JOIN 
suppliers ON products.supplier_id = suppliers.supplier_id;

-- #########################################################################################

-- 2) Create a stored procedure that takes the start and end dates of the sales and 
-- display all the sales transactions between the start and the end dates.

-- Depending how you would interpret this question.
-- Either, the stored procedure displays all sales transactions between the 
-- earliest date in the table (start date) and the most recent date in the table (end date),
-- ordered from earliest date to most recent date.

DELIMITER //
CREATE PROCEDURE displayAllSalesTransactions()
BEGIN
	SELECT * 
    FROM transactions
    ORDER BY transaction_date;
END //

CALL displayAllSalesTransactions();

-- Or, the stored procedure takes in a start date and an end date as parameters, and displays
-- all transactions between these two dates inclusive, ordered by earliest to most recent.

DELIMITER //
CREATE PROCEDURE displaySalesPeriodTransactions(IN start_date datetime, end_date datetime)
BEGIN
	SELECT * 
    FROM transactions
    WHERE transaction_date >= start_date AND transaction_date <= end_date
    ORDER BY transaction_date;
END //

CALL displaySalesPeriodTransactions('2023-01-01', '2023-12-31');

-- #########################################################################################

-- 3) Create a view that shows the total number of items a customer buys from the business 
-- in October 2023 along with the total price (use group by)

CREATE VIEW october_customer_purchases AS
SELECT q2.customer_id, q2.first_name, q2.last_name, q1.total_qty_items, q2.total_price FROM
(
SELECT order_id, SUM(product_qty) AS total_qty_items
FROM order_items
GROUP BY order_id
) AS q1
JOIN
(
SELECT orders.order_id,  orders.customer_id, customers.first_name, customers.last_name, orders.total_price, 
transactions.transaction_date, transactions.transaction_status
FROM orders
JOIN customers ON orders.customer_id = customers.customer_id
JOIN transactions ON orders.order_id = transactions.order_id
) AS q2
ON q1.order_id = q2.order_id
WHERE q2.transaction_date >= '2023-10-01' AND q2.transaction_date <= '2023-10-31' AND q2.transaction_status != 'Cancelled';

-- #########################################################################################

-- 4) Create a trigger that adjusts the stock level every time a product is sold

DELIMITER //
CREATE TRIGGER stock_update
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
	UPDATE products
    SET products.qty_on_hand = products.qty_on_hand - NEW.product_qty
	WHERE products.product_id = NEW.product_id;
END //
DELIMITER ;

-- #########################################################################################

-- 5) Create a report of the annual sales (2023) of the business showing the total number of 
-- products sold and the total price sold every month (use A group by with roll-up)

CREATE TEMPORARY TABLE sales_figures 
SELECT q1.order_id, MONTHNAME(q2.transaction_date) as sales_month, q1.total_qty_items, q2.total_price FROM 
(
SELECT order_id, SUM(product_qty) AS total_qty_items
FROM order_items
GROUP BY order_id
) AS q1
JOIN
(
SELECT orders.order_id,  orders.customer_id, customers.first_name, customers.last_name, 
transactions.transaction_date, orders.total_price, transactions.transaction_status
FROM orders
JOIN customers ON orders.customer_id = customers.customer_id
JOIN transactions ON orders.order_id = transactions.order_id
) AS q2
ON q1.order_id = q2.order_id
WHERE transaction_date >= '2023-01-01' AND transaction_date <= '2023-12-31' AND transaction_status != 'Cancelled';

SELECT COALESCE (sales_month, 'Annual Total') AS sales_month,
SUM(total_qty_items) AS total_items_sold,
ROUND(SUM(total_price),2)AS total_sales
FROM sales_figures
GROUP BY sales_month WITH ROLLUP;

-- #########################################################################################

-- 6) Display the growth in sales/services (as a percentage) for your business, from the 1st month of opening until now. 

-- Sales Growth = (Most Recent Month Total Sales - 1st Month Total Sales / 1st Month Total Sales) x 100

-- Create temp table to hold total monthly sales for January, and December respectively
CREATE TEMPORARY TABLE sales_jan_dec
SELECT * FROM
(SELECT sales_month, ROUND(SUM(total_price),2) AS monthly_sales
FROM sales_figures
GROUP BY sales_month
) AS q1
WHERE sales_month = 'January' OR sales_month = 'December';

-- Create temp table to calculate and hold difference between January sales and December sales amounts
CREATE TEMPORARY TABLE sales_growth
SELECT sales_month, monthly_sales, 
monthly_sales-LAG(monthly_sales) OVER (ORDER BY sales_month desc) AS sales_difference
FROM sales_jan_dec;

-- Create table to calculate and hold this difference as a fraction
CREATE TEMPORARY TABLE fractional_sales_growth
SELECT sales_month, monthly_sales, sales_difference,
sales_difference/LAG(monthly_sales) OVER (ORDER BY sales_month desc) AS sales_fraction
FROM sales_growth;

-- Display percentage sales growth by multiplying the fractional difference by 100
SELECT ROUND(sales_fraction * 100,2) AS percentage_sales_growth
FROM fractional_sales_growth
WHERE sales_month = 'December';

-- #########################################################################################

-- 7) Delete all customers who never buy a product from the business

-- Create a temp table with the customer IDs of customers with at least one order fulfilled
CREATE TEMPORARY TABLE customers_qty_orders
SELECT customer_id, 
	SUM(CASE 
		WHEN fulfilled_at IS NULL THEN 0
		ELSE 1
		END) 
	AS total_orders
	FROM orders
    GROUP BY customer_id;
    
-- Join the temp table with the customers table, and delete all customers where 
-- total_orders is null, i.e. they have never placed an order
DELETE customers FROM customers
LEFT JOIN customers_qty_orders ON customers.customer_id = customers_qty_orders.customer_id
WHERE customers_qty_orders.total_orders IS NULL;