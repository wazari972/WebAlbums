./download_a_theme.py  5 Martinique &
./download_a_theme.py 4 Vayrac &
./download_a_theme.py 11 Mariage &

for job in `jobs -p`
do
echo $job
    wait $job || let "FAIL+=1"
done
