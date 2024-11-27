# Use a reporting tool of your choice (e.g. Excel) to connect to your data 
# and create a graphical representation of the result of question 6 in Part 3.

# import necessary modules
import mysql.connector
import pandas as pd
import matplotlib.pyplot as plt

# establish a connection to the database
db = mysql.connector.connect(
  host="localhost",
  user="root",
  password="password",
  database="beauty_retailer"
)

cursor = db.cursor()

# define the query to be run
query = """SELECT q1.order_id, MONTHNAME(q2.transaction_date) as sales_month, q1.total_qty_items, q2.total_price FROM 
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
WHERE transaction_date >= '2023-01-01' AND transaction_date <= '2023-12-31' AND transaction_status != 'Cancelled';"""

# execute the query
cursor.execute(query)

# import the results of the query into a Pandas DataFrame
sales_figures_df = pd.DataFrame(cursor.fetchall())

# close the connection to the database
cursor.close()

# set DataFrame column names
sales_figures_df.columns = ["order_id","sales_month","total_qty_items","total_price"]

# calculate monthly total sales figures
monthly_sales_totals = sales_figures_df.groupby('sales_month')['total_price'].sum()

# define the order of months
month_order = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']

# convert the index (month names) to a categorical data type with the defined order
monthly_sales_totals.index = pd.Categorical(monthly_sales_totals.index, categories=month_order, ordered=True)

# sort the monthly sales totals DataFrame based on the categorical order
monthly_sales_totals = monthly_sales_totals.sort_index()

# plot the bar chart of monthly sales totals
plt.figure(figsize=(10, 6))
plt.bar(monthly_sales_totals.index, monthly_sales_totals.values, color='skyblue')
plt.xlabel('Month')
plt.ylabel('Total Sales')
plt.title('Monthly Sales Totals 2023')
plt.xticks(rotation=45)  # rotate x-axis labels for better readability
plt.tight_layout()  # adjust layout to prevent clipping of labels
plt.show()

# get the total sales for January and December
january_sales = monthly_sales_totals.loc['January']
december_sales = monthly_sales_totals.loc['December']

# calculate the percentage change
percentage_change = ((december_sales - january_sales) / january_sales) * 100

# plot the bar graph of January vs December sales totals
plt.figure(figsize=(8, 5))
bars = plt.bar(['January', 'December'], [january_sales, december_sales], color=['skyblue', 'lightcoral'])
plt.xlabel('Month')
plt.ylabel('Total Sales')
plt.title('Total Sales in January 2023 vs December 2023')
plt.ylim(0, max(january_sales, december_sales) * 1.2)  # adjust ylim to give space for the text

# add text above each bar showing monthly sales total
for bar in bars:
    yval = bar.get_height()
    plt.text(bar.get_x() + bar.get_width()/2, yval + 0.02*max(january_sales, december_sales), round(yval), ha='center', va='bottom')

# add text showing percentage change
plt.text(0.5, max(january_sales, december_sales) * 1.1, f'Percentage Change: {percentage_change:.2f}%', ha='center', va='bottom', fontsize=10, fontweight='bold')

plt.tight_layout()
plt.show()