<card>
    <div class="card">
        <div class="card-block">
            <h4 class="card-title">{ title }</h4>
            <yield from="content"/>
        </div>
    </div>

    <script>
        this.title = opts.title;
    </script>
</card>